package flightpath.com.mapmodule;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flightpathcore.network.SynchronizationHelper;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-10.
 */
@EViewGroup(resName = "widget_sync_status")
public class SyncStatusWidget extends LinearLayout implements SynchronizationHelper.SynchronizationCallback {

    @ViewById
    protected TextView lastSyncValue, toSendValue;
    @ViewById
    protected LinearLayout syncStateContainer;

    private SynchronizationHelper.SyncState currentState = null;

    public SyncStatusWidget(Context context) {
        super(context);
    }

    public SyncStatusWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SyncStatusWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SyncStatusWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(SynchronizationHelper.getInstance() != null) {
            SynchronizationHelper.getInstance().addListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(SynchronizationHelper.getInstance() != null) {
            SynchronizationHelper.getInstance().removeListener(this);
        }
    }

    @UiThread
    @Override
    public void onSynchronization(int eventsLeft, String dateOfLastSuccessfulSynchronization, SynchronizationHelper.SyncState syncState) {
        toSendValue.setText(eventsLeft+"");
        lastSyncValue.setText(dateOfLastSuccessfulSynchronization);
        if(currentState != syncState) {
            currentState = syncState;
            switch (syncState) {
                case STATE_OK:
                    syncStateContainer.setBackgroundResource(R.color.green_transparent);
                    break;
                case STATE_LAST_SYNC_FAILED:
                    syncStateContainer.setBackgroundResource(R.color.yellow_transparent);
                    break;
                case STATE_SYNC_FAILED:
                    syncStateContainer.setBackgroundResource(R.color.red_transparent);
                    break;

            }
        }
    }
}
