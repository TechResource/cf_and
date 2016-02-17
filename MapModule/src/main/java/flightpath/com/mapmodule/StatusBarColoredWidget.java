package flightpath.com.mapmodule;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(resName = "widget_status_bar_colored")
public class StatusBarColoredWidget extends RelativeLayout {

    @ViewById
    protected TextView contentTextView;
    @ViewById
    protected RelativeLayout root;

    public StatusBarColoredWidget(Context context) {
        super(context);
    }

    public StatusBarColoredWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarColoredWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusBarColoredWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void loadViews() {
        Utilities.setOswaldTypeface(getContext().getAssets(), contentTextView);
//        if (!isInEditMode()) {
//            Typeface typeBold = Typeface.createFromAsset(getContext().getAssets(), "Oswald-Bold.ttf");
//            contentTextView.setTypeface(typeBold);
//        }
    }

    public void setPrepareTrip() {
        contentTextView.setText(R.string.create_trip_label);
        root.setBackgroundColor(getResources().getColor(R.color.blue_color));
    }

    public void setDuringTrip() {
        contentTextView.setText(R.string.trip_in_progress_label);
        root.setBackgroundColor(getResources().getColor(R.color.blue_bg));
    }

    public void setPausedTrip() {
        contentTextView.setText(R.string.trip_paused_label);
        root.setBackgroundColor(getResources().getColor(R.color.yellow_bg));
    }

    public void setReadyToStartTrip() {
        contentTextView.setText(R.string.trip_ready_to_start_label);
        root.setBackgroundColor(getResources().getColor(R.color.yellow_bg));
    }

    public void setTextContent(String text) {
        contentTextView.setText(text);
    }

}
