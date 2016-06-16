package flightpath.com.mapmodule;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.PointsTable;
import com.flightpathcore.objects.PointObject;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-21.
 */
@EFragment(resName = "fragment_map")
public class MapFragment extends BaseFragment implements TripStatusHelper.TripStatusListener, LocationHandler.LocationAdditionalListener {

    private static final int PERMISSION_REQUEST = 1;

    private static final String[] permissionNeed = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @ViewById
    protected StatusBarColoredWidget tripStatusLabel;
    @ViewById
    protected MapView mapView;
    @ViewById
    protected ToggleButton trackLocationBtn;
    @ViewById
    protected TripStartStopWidget tripStartStopWidget;
    @ViewById
    protected TextView statusLabel, gpStatus, internetStatus;
    @Inject
    protected TripStatusHelper tripStatusHelper;
    @Inject
    protected LocationHandler locationHandler;

    private MapPathHelper mapPathHelper;
    private PointObject currentPoint = null;
    private TripObject currentTrip = null;
//    private int driverId = -1;
    private MapCallbacks mapCallbacks;
    private boolean startTripWithoutLocation = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIMapModule.diMapModule().injections().inject(this);

    }

    public void setCallbacks(MapCallbacks mapCallbacks, boolean startTripWithoutLocation){
        this.startTripWithoutLocation = startTripWithoutLocation;
        this.mapCallbacks = mapCallbacks;
    }

    public void setCallbacks(MapCallbacks mapCallbacks){
        this.mapCallbacks = mapCallbacks;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            initLocationHandler();
        }
        checkGpsStatus();
        if (locationHandler.isReady()) {
            locationHandler.addAdditionalListener(this);
            locationHandler.addAdditionalListener(mapPathHelper);
        }
        tripStatusHelper.addListener(this);
        tripStatusHelper.addListener(mapPathHelper);
    }

    private void initLocationHandler() {
        locationHandler.initLocationHandler(getActivity());
        locationHandler.addAdditionalListener(this);
    }

    public void setStatusLabel(String status){
        if(status != null && !status.isEmpty()) {
            statusLabel.setVisibility(View.VISIBLE);
            statusLabel.setText(getString(R.string.status_label) + ": " + status);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tripStatusHelper.removeListener(this);
        tripStatusHelper.removeListener(mapPathHelper);
        if (locationHandler.isReady()) {
            locationHandler.removeAdditionalListener(this);
            locationHandler.removeAdditionalListener(mapPathHelper);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(gpsReceiver, new IntentFilter("android.location.GPS_ENABLED_CHANGE"));
        getActivity().registerReceiver(networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(gpsReceiver);
        getActivity().unregisterReceiver(networkReceiver);

    }

    @AfterViews
    protected void init() {
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(13);
        mapView.getController().setCenter(new GeoPoint(51.507, -0.128));
        mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        if(mapPathHelper == null) {
            mapPathHelper = new MapPathHelper(mapView);
        }
    }

    @Click
    protected void tripStatusLabel() {
        if (currentTrip == null || currentTrip.getStatusEnum() == TripObject.TripStatus.TRIP_STOPPED || currentTrip.getStatusEnum() == null) {
            if(locationHandler.getLocation() != null || startTripWithoutLocation) {
                if(mapCallbacks != null){
                    mapCallbacks.onPrepareTrip();
                }
            }else{
                Toast.makeText(getActivity(), R.string.no_location_error, Toast.LENGTH_LONG).show();
            }
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    initLocationHandler();
                    return;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationHandler.onDestroy();
    }


    @Override
    public void onTripStatusChanged(TripObject.TripStatus tripStatus, TripObject trip) {
        currentTrip = trip;
        switch (tripStatus) {
            case TRIP_PAUSED:
                tripStartStopWidget.setVisibility(View.VISIBLE);
                tripStatusLabel.setPausedTrip();
                break;
            case TRIP_STARTED:
                tripStartStopWidget.setVisibility(View.VISIBLE);
                tripStatusLabel.setDuringTrip();
                break;
            case TRIP_STOPPED:
                tripStartStopWidget.setVisibility(View.GONE);
                tripStatusLabel.setPrepareTrip();
                break;
        }
    }

    @CheckedChange
    protected void trackLocationBtn() {
        if (trackLocationBtn.isChecked() && currentPoint != null) {
            mapView.getController().animateTo(new GeoPoint(currentPoint.latitude, currentPoint.longitude));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if(currentTrip != null) {
                currentPoint = new PointObject(location.getLatitude(), location.getLongitude(), currentTrip.tripId,
                        currentTrip.getStatusEnum() == TripObject.TripStatus.TRIP_PAUSED ? true : false);
                if (currentTrip.getStatusEnum() != TripObject.TripStatus.TRIP_STOPPED && currentTrip.tripId != 0) {
                    DBHelper.getInstance().insert(new PointsTable(), new PointsTable().getContentValues(currentPoint));
                }
                if(currentTrip.getStatusEnum() != TripObject.TripStatus.TRIP_STOPPED && currentTrip.startLat == 0){
                    tripStatusHelper.setTripStartLocation(location);
                }
            }
            if (trackLocationBtn.isChecked()) {
                mapView.getController().animateTo(new GeoPoint(location));
            }
        }
    }

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkGpsStatus();
        }
    };

    @UiThread
    protected void checkGpsStatus() {
        if(getContext() != null) {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpStatus.setVisibility(View.GONE);
            } else {
                gpStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                internetStatus.setVisibility(View.GONE);
            } else {
                internetStatus.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    public String getTitle() {
        return "TRIP DETAILS";
    }

    public boolean isOnTrip() {
        return tripStatusHelper.getTripStatus() == TripObject.TripStatus.TRIP_STARTED;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
