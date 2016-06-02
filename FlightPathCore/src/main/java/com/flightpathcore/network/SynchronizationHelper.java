package com.flightpathcore.network;

import android.content.Context;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.EventTable;
import com.flightpathcore.network.requests.SynchronizeRequest;
import com.flightpathcore.objects.DisposalObject;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-10.
 */
public class SynchronizationHelper {

    protected FPModel fpModel;
    private static SynchronizationHelper instance = null;
    private Thread syncThread = null;
    private int synPerMillis = 180 * 1000;
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
        if (instance == null) {
            instance = new SynchronizationHelper();
        }
        instance.fpModel = fpModel;

        instance.user = (UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID + "");
        if (instance.user != null) {
            instance.synPerMillis = instance.user.synchronizationPer * 1000;
        }
        if( instance.eventsSender == null || (instance.eventsSender != null && instance.eventsSender.lastSyncMillis != null && Utilities.getTimestamp() > instance.eventsSender.lastSyncMillis)) {
            if(instance.listeners == null)
                instance.listeners = new ArrayList<>();
            instance.eventsSender = new EventsSender();
            instance.shouldRunning = true;
            instance.syncThread = new Thread(instance.eventsSender);
            instance.syncThread.start();
            instance.eventsLeft = DBHelper.getInstance().countEventsLeft();
        }
    }

    public void stopThread() {
        shouldRunning = false;
        syncThread.interrupt();
    }

    public void addListener(SynchronizationCallback listener) {
        listeners.add(listener);
        listener.onSynchronization(eventsLeft, lastSyncDate, syncState);
    }

    public void removeListener(SynchronizationCallback listener) {
        while (listeners.remove(listener)) {
        }
    }

    public void notifyListeners() {
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
        public final Object lockObj = new Object();
        public Long lastSyncMillis = null;

        @Override
        public void run() {

            while (instance.shouldRunning) {

                if (!instance.sending) {
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
                            if (response != null) {
                                DBHelper.getInstance().setEventsSuccess(eventsToSend);
                            }
                        } else {
                            response = 1;
                        }

                        Integer imgResponse = 1;
                        Integer disposalResponse = 1;
                        if (response != null) {
                            EventObject lastSendEvent = DBHelper.getInstance().getLastSendEvent();
                            if (lastSendEvent != null) {
                                Map<String, TypedFile> output = createMultipartOutput(lastSendEvent.eventId);
                                if (output != null) {
                                    try {
                                        imgResponse = instance.fpModel.fpApi.sendMultipleFiles(output);
                                        if (imgResponse != null) {
                                            List<ItemsDamagedObject> items = DBHelper.getInstance().getDamagedItemsToSend(lastSendEvent.eventId);
                                            for (ItemsDamagedObject e : items) {
                                                DBHelper.getInstance().markDamagedItemsAsSent(e.id);
                                            }
                                        }
                                    } catch (RetrofitError e) {
                                        imgResponse = null;
                                    }
                                } else {
                                    imgResponse = 1;
                                }

                                Map<String, TypedFile> outputDisposalInspection = createMultipartOutputDisposalInspection(lastSendEvent.eventId);
                                if (outputDisposalInspection != null) {
                                    try {
                                        disposalResponse = instance.fpModel.fpApi.sendMultipleFiles(outputDisposalInspection);
                                        if (disposalResponse != null) {
                                            for (DisposalObject disposalObject : DBHelper.getInstance().getDisposalInspectionToSend(lastSendEvent.eventId)) {
                                                DBHelper.getInstance().markDisposalAsSent(disposalObject.id);
                                            }
                                        }
                                    } catch (RetrofitError e) {
                                        disposalResponse = null;
                                    }
                                }else{
                                    disposalResponse = 1;
                                }

                            } else {
                                imgResponse = 1;
                            }
                        }

                        instance.lastSyncDate = Utilities.getCurrentDateFormatted();

                        if (response != null && imgResponse != null && disposalResponse != null) {
                            instance.syncState = SyncState.STATE_OK;
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
                    lastSyncMillis = Utilities.getTimestamp();
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

        private Map<String, TypedFile> createMultipartOutput(long eventIdTo) {

            List<ItemsDamagedObject> items = DBHelper.getInstance().getDamagedItemsToSend(eventIdTo);

            if (items == null || items.size() == 0) {
                return null;
            }

            Map<String, TypedFile> multipartTypedOutput = new HashMap<>();
            for (int i = 0; i < items.size(); i++) {
                File f = new File(items.get(i).imagePath.replace("file:", ""));
                EventObject event = (EventObject) DBHelper.getInstance().get(new EventTable(), items.get(i).eventId + "");
                if (event != null) {
                    multipartTypedOutput.put(("damage_item_id[" + event.timestamp + "|" + items.get(i).id + "]"), new TypedFile("image/jpg", f));
                }
            }
            return multipartTypedOutput;
        }

        private Map<String, TypedFile> createMultipartOutputDisposalInspection(long eventIdTo) {

            List<DisposalObject> items = DBHelper.getInstance().getDisposalInspectionToSend(eventIdTo);

            if (items == null || items.size() == 0) {
                return null;
            }

            Map<String, TypedFile> multipartTypedOutput = new HashMap<>();
            for (int i = 0; i < items.size(); i++) {
//                for (String imgPath : items.get(i).imagePaths) {
                for (int p = 0; p < items.get(i).imagePaths.size(); p++) {
                    File f = new File(items.get(i).imagePaths.get(p).replace("file:", ""));
                    EventObject event = (EventObject) DBHelper.getInstance().get(new EventTable(), items.get(i).eventId + "");
                    if (event != null) {
                        multipartTypedOutput.put(("disposal_photo[" + event.timestamp + "|" + items.get(i).id + "|" + (p+1) + "]"), new TypedFile("image/jpg", f));
                    }
                }
            }
            return multipartTypedOutput;
        }

    }

    public void updateCounter() {
        instance.eventsLeft = DBHelper.getInstance().countEventsLeft();
        instance.notifyListeners();
    }

    public void sendNow(Context context) {
        if (eventsSender == null) {
            initInstance(new FPModel(context));
        }
        if (eventsSender != null && !sending) {
            synchronized (eventsSender.lockObj) {
                eventsSender.lockObj.notify();
            }
        }
    }

    public interface SynchronizationCallback {
        void onSynchronization(int eventsLeft, String dateOfLastSuccessfulSynchronization, SyncState syncState);
    }

    public enum SyncState {
        STATE_OK,
        STATE_LAST_SYNC_FAILED,
        STATE_SYNC_FAILED
    }
}
