package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flightpathcore.objects.ItemsDamagedObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-11.
 */
@EViewGroup(resName = "widget_damage")
public class DamageWidget extends RelativeLayout {

    @ViewById
    public TextView removeDmg, description;
    @ViewById
    public ImageView dmgImg;
    private ItemsDamagedObject damage;

    public DamageWidget(Context context) {
        super(context);
    }

    public DamageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DamageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DamageWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDamage(ItemsDamagedObject damage){
        this.damage = damage;
        if(dmgImg != null) {
            fillView();
        }
    }

    @AfterViews
    protected void init(){
        if(damage != null){
            fillView();
        }
    }

    private void fillView(){
        ImageLoader.getInstance().displayImage(damage.imagePath, dmgImg);
        description.setText(damage.dmgDescription);
    }

    public Long getDmgId() {
        return damage.id;
    }

}
