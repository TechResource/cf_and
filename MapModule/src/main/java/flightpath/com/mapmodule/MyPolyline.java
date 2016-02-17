package flightpath.com.mapmodule;

import android.content.Context;

import com.flightpathcore.objects.PointObject;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-09.
 */
public class MyPolyline extends Polyline {

    private int pointsNumber = 0;

    public MyPolyline(Context ctx) {
        super(ctx);
    }

    public MyPolyline(ResourceProxy resourceProxy) {
        super(resourceProxy);
    }

    public void addPoint(GeoPoint geoPoint){
        pointsNumber++;
        super.addPoint(geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6());
    }

    public void clearPath(){
        pointsNumber = 0;
        super.clearPath();
    }

    public int getNumberOfPoints(){
        return pointsNumber;
    }

    public void setMyPoints(List<PointObject> points) {
        this.pointsNumber = points.size();
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (PointObject p : points){
            geoPoints.add(new GeoPoint(p.latitude, p.longitude));
        }
        setPoints(geoPoints);
    }
}
