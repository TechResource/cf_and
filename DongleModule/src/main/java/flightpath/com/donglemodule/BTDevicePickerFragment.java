package flightpath.com.donglemodule;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flightpathcore.adapters.ListAdapter;
import com.flightpathcore.adapters.ViewWrapper;
import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.objects.LoaderObject;
import com.flightpathcore.widgets.ProgressListItem_;
import com.flightpathcore.widgets.SimpleDividerItemDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import flightpath.com.donglemodule.di.DIDongleModule;


/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-13.
 */
@EFragment(resName = "fragment_bt_device_picker")
public class BTDevicePickerFragment extends BaseFragment implements BTReceiver.BTCallbacks, DongleDataHelper.DongleDataListener {

    private BluetoothAdapter bluetoothAdapter;
    public final static int REQUEST_ENABLE_BT = 100;
    protected BTReceiver btReceiver;
    @ViewById
    protected RecyclerView devicesList;
    @ViewById
    protected TextView status;
    @ViewById
    protected ProgressBar statusProgress;
    @Inject
    protected DongleDataHelper dongleDataHelper;

    private Adapter adapter;
    private ProgressDialog progressDialog;
    private boolean pairedDeviceFound = false;
    private ArrayList<BluetoothDevice> foundDevices = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIDongleModule.diDongleModule().injections().inject(this);
    }

    @AfterViews
    protected void init() {
        status.setText("scan devices");
        adapter = new Adapter();
        devicesList.setLayoutManager(new LinearLayoutManager(getContext()));
        devicesList.setAdapter(adapter);
        devicesList.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        adapter.setItems(new ArrayList<>());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        onBtEnabled(bluetoothAdapter.getState());
        checkBtEnabled();

        btReceiver = new BTReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(btReceiver, intentFilter);
    }

    private int errorCounter = 0;
    private String deviceWithError = null;

    @UiThread
    public void reset() {
        String pairedDevice = null;
        if(getContext() != null) {
            pairedDevice = DonglePreferences.getPairedDevice(getContext());
        }
        if(pairedDevice != null && !pairedDevice.isEmpty()) {
            if(deviceWithError == null){
                deviceWithError = pairedDevice;
                errorCounter = 0;
            }
            errorCounter++;
            if(errorCounter > 1) {
                status.setText("OBD service error (" + errorCounter + " attempts).\nPlease wait or select another device.");
            }else{
                status.setText("OBD service error.\nPlease wait or select another device.");
            }
        }else{
            status.setText("scan devices");
        }
        pairedDeviceFound = false;
        scanDevices();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(btReceiver);
    }

    private void checkBtEnabled() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanDevices();
        }
    }

    private void scanDevices() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        foundDevices.add(device);
        if (device.getAddress().equals(DonglePreferences.getPairedDevice(getContext()))) {
            onDeviceBound(device);
        }
        adapter.addItem(device);
    }

    @Override
    public void onDeviceBound(BluetoothDevice device) {
        if(errorCounter < 1) {
            status.setText("starting OBD service\n(" + device.getName() + ")");
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if(errorCounter <= 5) {
            DonglePreferences.savePairedDevice(getContext(), device.getAddress());
            dongleDataHelper.setPairedDevice(device);
            dongleDataHelper.startService(getActivity());
        }
    }

    private long lastDevicesClear = 0;

    @Override
    public void onDiscoveryFinished() {
        if(System.currentTimeMillis() > lastDevicesClear+60*1000) {
            lastDevicesClear = System.currentTimeMillis();
            List<BluetoothDevice> currentDevices = new ArrayList<>(adapter.getItems());
            for (BluetoothDevice d : currentDevices) {
                if (!foundDevices.contains(d)) {
                    adapter.removeItem(d);
                }
            }
            foundDevices.clear();
        }
        if (!pairedDeviceFound) {
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onBtEnabled(int state) {
        if(state == BluetoothAdapter.STATE_ON) {
            status.setText("scan devices");
            statusProgress.setVisibility(View.VISIBLE);
            devicesList.postDelayed(() -> scanDevices(), 1000);
        }else if(state == BluetoothAdapter.STATE_OFF){
            status.setText("Bluetooth turned off");
            statusProgress.setVisibility(View.GONE);
            adapter.setItems(new ArrayList<>());
        }
    }

    private void boundWithDevice(BluetoothDevice device) {
        try {
            status.setText("pairing with device " + device.getName());
            progressDialog = ProgressDialog.show(getActivity(), "Pairing devices", "Please wait...", true, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            } else {
                Method method = device.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(device, (Object[]) null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            scanDevices();
        }
    }

    @Override
    public void onDongleDataReceived(Map<String, String> dongleData) {
        if (dongleData != null) {
            pairedDeviceFound = true;
            bluetoothAdapter.cancelDiscovery();
        }
    }

    protected class Adapter extends ListAdapter<View, BluetoothDevice> {

        @Override
        public void addItem(BluetoothDevice item) {
            if (!items.contains(item)) {
                super.addItem(item);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) instanceof BluetoothDevice) {
                return 0;
            }
            throw new IllegalArgumentException("wrong object type: " + items.get(position).getClass().getName());
        }

        @Override
        protected View onCreateItemView(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return SingleDeviceWidget_.build(getContext());
                case 1:
                    return ProgressListItem_.build(getContext());
            }
            throw new IllegalArgumentException("wrong view type: " + viewType);
        }

        @Override
        public void onBindViewHolder(ViewWrapper<View> holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == 0) {
                ((SingleDeviceWidget) holder.getView()).setData((BluetoothDevice) items.get(position));
                holder.getView().getRootView().setOnClickListener(v -> {
                    errorCounter = 0;
                    deviceWithError = null;
                    if (((BluetoothDevice) items.get(position)).getBondState() == BluetoothDevice.BOND_BONDED) {
                        onDeviceBound((BluetoothDevice) items.get(position));
                    } else {
                        boundWithDevice((BluetoothDevice) items.get(position));
                    }
                });
            }
        }
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
