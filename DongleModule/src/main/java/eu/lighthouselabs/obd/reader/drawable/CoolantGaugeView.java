package eu.lighthouselabs.obd.reader.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import flightpath.com.donglemodule.R;

public class CoolantGaugeView extends GradientGaugeView {

    public final static int min_temp = 0;
    public final static int max_temp = 200;
    public final static int TEXT_SIZE = 18;
    public final static int range = max_temp - min_temp;
    private int temp = min_temp;

    public CoolantGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setTextSize(TEXT_SIZE);
        Typeface bold = Typeface.defaultFromStyle(Typeface.BOLD);
        paint.setTypeface(bold);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setTemp(int temp) {
        this.temp = temp;
        if (this.temp < min_temp) {
            this.temp = min_temp + 2;
        }
        if (this.temp > max_temp) {
            this.temp = max_temp;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Resources res = context.getResources();
        Drawable container = res.getDrawable(R.drawable.coolant_gauge);
        int width = getWidth();
        int left = getLeft();
        int top = getTop();
        paint.setColor(Color.BLUE);
        canvas.drawText("C", left, top + TEXT_SIZE, paint);
        paint.setColor(Color.RED);
        canvas.drawText("H", left + width - TEXT_SIZE, top + TEXT_SIZE, paint);
        drawGradient(canvas, container, 0, temp - min_temp, range);
    }
}
