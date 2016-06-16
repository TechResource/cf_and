package flightpath.com.donglemodule;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.api.SdkVersionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import flightpath.com.donglemodule.ChartsContainerFragment_;
import flightpath.com.donglemodule.di.DIDongleModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-13.
 */
@EFragment(resName = "fragment_dongle_container")
public class DongleContainerFragment extends BaseFragment implements DongleDataHelper.DongleServiceListener, DongleDataHelper.DongleDataListener{

    private static final int PERMISSION_REQUEST = 2;

    private static final String[] permissionNeed = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN };

    private BTDevicePickerFragment devicePickerFragment;
    private ChartsContainerFragment chartsContainerFragment;
    private boolean devicePickerShowing = false;
    @Inject
    protected DongleDataHelper dongleDataHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIDongleModule.diDongleModule().injections().inject(this);
        dongleDataHelper.setDongleServiceListener(this);
    }

    public DongleDataHelper getDongleDataHelper(){
        return dongleDataHelper;
    }

    @AfterViews
    protected void init(){
        checkPermissions();
        showDevicePicker();
    }

    private boolean checkPermissions() {
        List<String> permissionsRequest = new ArrayList<>();

        for (String p : permissionNeed) {
            if (ContextCompat.checkSelfPermission(getActivity(), p) != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(p);
            }
        }

        if (permissionsRequest.size() == 0) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissionsRequest.toArray(new String[permissionsRequest.size()]), PERMISSION_REQUEST);
            return false;
        }
    }

    @Override
    public void onDestroy() {
        dongleDataHelper.onDestroy(getActivity());
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFragmentManager.setAccessible(true);
                childFragmentManager.set(this, null);

            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.BLUETOOTH) ||
                        permissions[i].equalsIgnoreCase(Manifest.permission.BLUETOOTH_ADMIN)) {
                    showDevicePicker();
                    return;
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "VEHICLE";
    }

    @Override
    public void onDongleConnected() {
        if(getActivity() == null)
            return;
        if(devicePickerShowing) {
            if (chartsContainerFragment == null) {
                chartsContainerFragment = (ChartsContainerFragment) getChildFragmentManager().findFragmentByTag("chartsContainerFragment");
                if (chartsContainerFragment == null) {
                    chartsContainerFragment = ChartsContainerFragment_.builder().build();
                    getChildFragmentManager().beginTransaction()
                            .remove(devicePickerFragment)
                            .replace(R.id.fragmentContainer, chartsContainerFragment, "chartsContainerFragment")
                            .commitAllowingStateLoss();
                }
            } else {
                getChildFragmentManager().beginTransaction()
                        .remove(devicePickerFragment)
                        .replace(R.id.fragmentContainer, chartsContainerFragment, "chartsContainerFragment")
                        .commitAllowingStateLoss();
            }
        }
        devicePickerShowing = false;
    }

    @Override
    public void onDongleDisconnected(Throwable error, String extraInfo) {
//        if(getActivity() != null && getActivity().getApplication() != null)
//            ((BaseApplication)getActivity().getApplication()).logCrash(error, extraInfo);

        showDevicePicker();
        devicePickerFragment.reset();
    }

    private void showDevicePicker(){
        if(getActivity() == null || devicePickerShowing){
            return;
        }
        if(devicePickerFragment == null){
            devicePickerFragment = (BTDevicePickerFragment)getChildFragmentManager().findFragmentByTag("devicePickerFragment");
            if(devicePickerFragment == null){
                devicePickerFragment = BTDevicePickerFragment_.builder().build();
                getChildFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, devicePickerFragment, "devicePickerFragment")
                        .commitAllowingStateLoss();
            }
        }else{
            try {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, devicePickerFragment, "devicePickerFragment")
                        .commitAllowingStateLoss();
            }catch (Exception e){
                try {
                    getChildFragmentManager().beginTransaction()
                            .show(devicePickerFragment)
                            .commitAllowingStateLoss();
                }catch (Exception e2){

                }
            }
        }
        devicePickerShowing = true;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDongleDataReceived(Map<String, String> dongleData) {
        if(devicePickerShowing){
            showDevicePicker();
        }
    }
}
