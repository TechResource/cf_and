package com.flightpathcore.network;

import android.util.Log;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.EventTable;
import com.flightpathcore.database.tables.ItemsDamagedTable;
import com.flightpathcore.di.DICore;
import com.flightpathcore.network.requests.SynchronizeRequest;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-10.
 */
public class SynchronizationHelper {

    protected FPModel fpModel;
    private static SynchronizationHelper instance = null;
    private Thread syncThread = null;
    private int synPerMillis = 180*1000;
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
                        Integer response = null;
                        if (eventsToSend.size() > 0) {
                            try {
                                response = instance.fpModel.fpApi.sendEvents(new SynchronizeRequest(instance.user.tokenId, instance.user.access, eventsToSend));
                            } catch (RetrofitError e) {
                                response = null;
                            }
                        }else{
                            response = 1;
                        }

                        Integer imgResponse = 1;
                        if(response != null) {
                            EventObject lastSendEvent = DBHelper.getInstance().getLastSendEvent();
                            if (lastSendEvent != null) {
                                Map<String, TypedFile> output = createMultipartOutput(lastSendEvent.eventId);
                                if (output != null) {
                                    try {
                                        imgResponse = instance.fpModel.fpApi.sendMultipleFiles(output);
                                        if (imgResponse != null) {
                                            if (eventsToSend.size() > 0) {
                                                for (ItemsDamagedObject e : DBHelper.getInstance().getDamagedItemsToSend(eventsToSend.get(eventsToSend.size() - 1).eventId)) {
                                                    DBHelper.getInstance().removeDamagedItemById(e.id);
                                                }
                                            }
                                        }
                                    } catch (RetrofitError e) {
                                        imgResponse = null;
                                    }
                                } else {
                                    imgResponse = 1;
                                }
                            } else {
                                imgResponse = 1;
                            }
                        }

                        instance.lastSyncDate = Utilities.getCurrentDateFormatted();
                        if (response != null && imgResponse != null ) {
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

        private Map<String,TypedFile> createMultipartOutput( long eventIdTo) {

            List<ItemsDamagedObject> items = DBHelper.getInstance().getDamagedItemsToSend(eventIdTo);

            if( items == null || items.size() == 0){
                return null;
            }

            Map<String,TypedFile> multipartTypedOutput = new HashMap<>();
            for(int i=0;i<items.size();i++){
                File f = new File(items.get(i).imagePath.replace("file:",""));
                EventObject event = (EventObject) DBHelper.getInstance().get(new EventTable(), items.get(i).eventId+"");
                if (event != null) {
                    multipartTypedOutput.put(("damage_item_id[" + event.timestamp + "|" + items.get(i).id + "]"), new TypedFile("image/jpg", f));
                }
            }
            return multipartTypedOutput;
        }

    }

    public void updateCounter(){
        instance.eventsLeft = DBHelper.getInstance().countEventsLeft();
        instance.notifyListeners();
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
