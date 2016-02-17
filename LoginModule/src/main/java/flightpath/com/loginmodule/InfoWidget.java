package flightpath.com.loginmodule;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-08.
 */
@EViewGroup(resName = "widget_info")
public class InfoWidget extends RelativeLayout {

    @ViewById
    protected TextView text;

    private String textResource;
    private Drawable imageResource;

    public InfoWidget(Context context) {
        super(context);
    }

    public InfoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init(){
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"Oswald-Regular.ttf");
        text.setTypeface(type);
        if(textResource != null){
            fillView();
        }
    }

    @SuppressWarnings("deprecation")
    private void fillView(){
        if(imageResource != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(imageResource);
            } else {
                setBackgroundDrawable(imageResource);
            }
        }
        text.setText(textResource);
    }

    public void setData(String textResource, Drawable imageResource){
        this.textResource = textResource;
        this.imageResource = imageResource;

        if(text != null){
            fillView();
        }
    }

}
