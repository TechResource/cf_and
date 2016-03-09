package flightpath.com.inspectionmodule.squashedFrog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.CollectionDamagesTable;
import com.flightpathcore.objects.CollectionDamagesObject;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.R;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-07.
 */
public class SquashedFrogPanelView extends View implements View.OnTouchListener {

    private Canvas canvas;
    private List<CollectionDamagesObject> damages;
    private Paint paint;
    private CollectionDamagesObject.CollectionType squashedFrogType;
    private SquashedFrogCallback callback;
    private int circleRadius = 0;
    private Long eventId;

    public SquashedFrogPanelView(Context context) {
        super(context);
        init();
    }

    public SquashedFrogPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circleRadius = getContext().getResources().getDimensionPixelSize(R.dimen.circle_radius);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(18);
        paint.setColor(Color.GREEN);

        canvas = new Canvas();
    }

    public void setData(CollectionDamagesObject.CollectionType squashedFrogType, SquashedFrogCallback callback, Long eventId, List<CollectionDamagesObject> collections){
        this.eventId = eventId;
        this.squashedFrogType = squashedFrogType;
        this.callback = callback;
        damages = new ArrayList<>();
        for (CollectionDamagesObject c : collections){
            if(c.collectionType == squashedFrogType){
                c.x = null;
                c.y = null;
                damages.add(c);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (CollectionDamagesObject d : damages) {
            if(d.x == null || d.y == null){
                d.x = Float.valueOf((d.xPercent * getMeasuredWidth())/100);
                d.y = Float.valueOf((d.yPercent * getMeasuredHeight())/100);
            }
            canvas.drawCircle(d.x, d.y, circleRadius, paint);
        }

    }

    private void onDmgAdd(float x, float y) {
        boolean found = false;
        for (CollectionDamagesObject d : damages){
            if( Math.abs(d.x-x) < 3*circleRadius && Math.abs(d.y-y) < 3*circleRadius ) {
                found = true;
                callback.showDmgsDetails(d);
                break;
            }
        }
        if(!found) {
            CollectionDamagesObject cObject = new CollectionDamagesObject(x, y, eventId);
            cObject.xPercent = (int) ((x/getMeasuredWidth())*100);
            cObject.yPercent = (int) ((y/getMeasuredHeight())*100);
            cObject.collectionType = squashedFrogType;
            CollectionDamagesTable cT = new CollectionDamagesTable();
            cObject.id = DBHelper.getInstance().insert(cT, cT.getContentValues(cObject));
            damages.add(cObject);
            callback.showDmgsDetails(cObject);
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onDmgAdd(event.getX(), event.getY());
                break;
        }
        return true;
    }

    public List<CollectionDamagesObject> getCollection() {
        return damages;
    }

    public void refresh(List<CollectionDamagesObject> collections) {
        setData(squashedFrogType, callback, eventId, collections);
        invalidate();
    }

    public interface SquashedFrogCallback{
        void showDmgsDetails(CollectionDamagesObject damageCollection);
    }
}
