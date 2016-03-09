package flightpath.com.inspectionmodule.squashedFrog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.utilities.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import flightpath.com.inspectionmodule.InspectionsUtilities;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-08.
 */
@EViewGroup(resName = "widget_single_damage")
public class SingleDamageWidget extends RelativeLayout{

    @ViewById
    protected ImageView damageImage;

    private ItemsDamagedObject damage;

    public SingleDamageWidget(Context context) {
        super(context);
    }

    public SingleDamageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleDamageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleDamageWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init(){
        if(damage != null){
            fillView();
        }
    }

    public void setData(ItemsDamagedObject damage){
        this.damage = damage;
        if(damageImage != null){
            fillView();
        }
    }

    private void fillView(){
        ImageLoader.getInstance().displayImage(damage.imagePath, damageImage);
    }

}
