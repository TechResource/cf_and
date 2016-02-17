package flightpath.com.mapmodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.PointsTable;
import com.flightpathcore.objects.PointObject;
import com.flightpathcore.objects.TripObject;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

import javax.inject.Inject;

public class MapPathHelper implements TripStatusHelper.TripStatusListener, LocationHandler.LocationAdditionalListener {

    private final int MAX_POINTS_IN_POLYLINE = 750;

    private MyPolyline currentOverlay = null;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay = null;
    private boolean collectingPoints = false;
    private Long tripId = null;

    public MapPathHelper(MapView mapView) {
        this.mapView = mapView;
        currentOverlay = new MyPolyline(mapView.getContext());
        currentOverlay.setColor(Color.BLUE);
        currentOverlay.setWidth(5.0f);
        mapView.getOverlays().add(currentOverlay);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && collectingPoints) {
            currentOverlay.addPoint(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onTripStatusChanged(TripObject.TripStatus tripStatus, TripObject trip) {
        switch (tripStatus) {
            case TRIP_STARTED:
                if (!collectingPoints) {
                    currentOverlay.clearPath();
                }
            case TRIP_PAUSED:
                collectingPoints = true;
                removeMyPosition();
                break;
            case TRIP_STOPPED:
                collectingPoints = false;
                addMyPosition();
                break;
        }
        if(trip != null && tripId != trip.tripId) {
            this.tripId = trip.tripId;
            getPoints();
        }
    }

    private void getPoints() {
        PointsTable pt = new PointsTable();
        currentOverlay.setMyPoints((List<PointObject>) DBHelper.getInstance().getMultiple(pt,
                tripId+"", pt.getIdColumn() + " DESC", MAX_POINTS_IN_POLYLINE ));
    }

    private void addMyPosition() {
        if (myLocationOverlay == null) {
            myLocationOverlay = new MyLocationNewOverlay(mapView.getContext(), new GpsMyLocationProvider(mapView.getContext()), mapView);
            myLocationOverlay.disableFollowLocation();
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.setDrawAccuracyEnabled(true);
            mapView.getOverlays().add(myLocationOverlay);
        }
    }

    private void removeMyPosition() {
        if (myLocationOverlay != null) {
            mapView.getOverlays().remove(myLocationOverlay);
            myLocationOverlay = null;
        }
    }

    /**
     * disabled for now, maybe in future we will provide custom icons
     */
    public class MyProxyResouce extends DefaultResourceProxyImpl {

        public MyProxyResouce(Context pContext) {
            super(pContext);
        }

        @Override
        public Bitmap getBitmap(bitmap pResId) {
            if(pResId == bitmap.person){
                return BitmapFactory.decodeResource(mapView.getContext().getResources(), R.drawable.person);
            }else {
                return super.getBitmap(pResId);
            }
        }
    }

}
