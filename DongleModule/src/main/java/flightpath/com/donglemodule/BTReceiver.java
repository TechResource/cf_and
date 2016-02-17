package flightpath.com.donglemodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-27.
 */
public class BTReceiver extends BroadcastReceiver {

    public static final String DONGLE_PAIRED_DEVICE_KEY = "dongle_paired_device_key";
    private BTCallbacks callbacks;

    public BTReceiver(BTCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.d("BTRECEIVER", action);
        if(BluetoothDevice.ACTION_FOUND.equals(action)){ // some device found
            callbacks.onDeviceFound(device);
        }else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){ //connected with device
        }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){ //device pair state changed
            if(intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR) == BluetoothDevice.BOND_BONDED) {
                callbacks.onDeviceBound(device);
            }
        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            callbacks.onDiscoveryFinished();
        }else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){ //connection lost
            callbacks.onDiscoveryFinished();
        }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            callbacks.onBtEnabled(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF));
        }
    }

    public interface BTCallbacks{
        void onDeviceFound(BluetoothDevice device);
        void onDeviceBound(BluetoothDevice device);
        void onDiscoveryFinished();
        void onBtEnabled(int state);
    }
}
