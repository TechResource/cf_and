package flightpath.com.donglemodule;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-07.
 */
@EViewGroup(resName = "single_device_widget")
public class SingleDeviceWidget extends LinearLayout {

    @ViewById
    protected TextView deviceName;
    private BluetoothDevice device;

    public SingleDeviceWidget(Context context) {
        super(context);
    }

    public SingleDeviceWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleDeviceWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleDeviceWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init(){
        if(device != null){
            fillView();
        }
    }

    private void fillView(){
        deviceName.setText(device.getName());
    }

    public void setData(BluetoothDevice device){
        this.device = device;
        if(deviceName != null){
            fillView();
        }
    }
}
