package com.flightpathcore.network;

import android.util.Log;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.EventTable;
import com.flightpathcore.di.DICore;
import com.flightpathcore.network.requests.SynchronizeRequest;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-10.
 */
public class SynchronizationHelper {

    protected FPModel fpModel;
    private static SynchronizationHelper instance = null;
    private Thread syncThread = null;
    private int synPerMillis = 10000;
    private boolean shouldRunning = true;
    private UserObject user;
    private List<SynchronizationCallback> listeners;
    private int eventsLeft = 0;
    private String lastSyncDate = "-";
    private SyncState syncState = SyncState.STATE_OK;
    private boolean sending = false;
    private EventsSender eventsSender;

    public static SynchronizationHelper getInstance() {
        return instance;
    }

    public static void initInstance(FPModel fpModel) {
        if(instance == null) {
            instance = new SynchronizationHelper();
        }
        instance.fpModel = fpModel;

        instance.listeners = new ArrayList<>();
        instance.user = (UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID+"");
        if(instance.user != null){
            instance.synPerMillis = instance.user.synchronizationPer * 1000;
        }
        instance.eventsSender = new EventsSender();
        instance.syncThread = new Thread(instance.eventsSender);
        instance.syncThread.start();
        instance.eventsLeft = DBHelper.getInstance().countEventsLeft();
    }

    public void stopThread(){
        shouldRunning = false;
        syncThread.interrupt();
    }

    public void addListener(SynchronizationCallback listener){
        listeners.add(listener);
        listener.onSynchronization(eventsLeft, lastSyncDate, syncState);
    }

    public void removeListener(SynchronizationCallback listener){
        while(listeners.remove(listener)){}
    }

    public void notifyListeners(){
        for (SynchronizationCallback l : listeners) {
            l.onSynchronization(eventsLeft, lastSyncDate, syncState);
        }
    }

    public void increaseEventCounter() {
        eventsLeft++;
        notifyListeners();
    }

    private static class EventsSender implements Runnable {
        private static final int MAX_EVENTS_TO_SEND = 500;
        private List<EventObject> eventsToSend;
        private EventTable eventTable;
        public final Object lockObj = new Object();

        @Override
        public void run() {

            eventTable = new EventTable();

            while (instance.shouldRunning){
                if(!instance.sending) {
                    instance.sending = true;
                    if (instance.user == null) {
                        instance.user = (UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID + "");
                        if (instance.user != null) {
                            instance.synPerMillis = instance.user.synchronizationPer * 1000;
                        }
                    } else {
                        eventsToSend = DBHelper.getInstance().getEventsToSend(MAX_EVENTS_TO_SEND);
//                    eventsToSend = (List<EventObject>) DBHelper.getInstance().getMultiple(eventTable, null, eventTable.getIdColumn(), MAX_EVENTS_TO_SEND);

                        if (eventsToSend.size() > 0) {
                            Integer response = null;
                            try {
                                response = instance.fpModel.fpApi.sendEvents(new SynchronizeRequest(instance.user.tokenId, instance.user.access, eventsToSend));
                            } catch (RetrofitError e) {

                            }

                            instance.lastSyncDate = Utilities.getCurrentDateFormatted();
                            if (response != null) {
                                instance.syncState = SyncState.STATE_OK;
                                DBHelper.getInstance().setEventsSuccess(eventsToSend);
                            } else {
                                if (instance.syncState == SyncState.STATE_OK) {
                                    instance.syncState = SyncState.STATE_LAST_SYNC_FAILED;
                                } else {
                                    instance.syncState = SyncState.STATE_SYNC_FAILED;
                                }
                            }
                            instance.eventsLeft = DBHelper.getInstance().countEventsLeft();
                            instance.notifyListeners();
                        }
                    }
                    instance.sending = false;
                }
                synchronized (lockObj) {
                    try {
                        lockObj.wait(instance.synPerMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendNow(){
        if(eventsSender != null && !sending){
            synchronized (eventsSender.lockObj) {
                eventsSender.lockObj.notify();
            }
        }
    }

    public interface SynchronizationCallback{
        void onSynchronization(int eventsLeft, String dateOfLastSuccessfulSynchronization, SyncState syncState );
    }

    public enum SyncState{
        STATE_OK,
        STATE_LAST_SYNC_FAILED,
        STATE_SYNC_FAILED
    }
}
