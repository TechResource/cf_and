package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.ItemsDamagedTable;
import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.squashedFrog.SquashedFrogActivity;
import flightpath.com.inspectionmodule.squashedFrog.SquashedFrogActivity_;
import flightpath.com.inspectionmodule.widgets.objects.DamagesObject;
import flightpath.com.inspectionmodule.widgets.objects.DamagesWithSquashedFrogObject;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EViewGroup(resName = "widget_inspection_damages_with_squashed_frog")
public class DamagesWithSquashedFrogWidget extends LinearLayout implements InspectionWidgetInterface<DamagesWithSquashedFrogObject>{

    @ViewById
    protected Button addDamage;

    private DamagesWidget.DamagesCallback damagesCallback;
    protected AlertDialog damageDialog = null;
    private boolean damageDialogShouldBeOpen = false;
    private DamagesWithSquashedFrogObject data;

    public DamagesWithSquashedFrogWidget(Context context) {
        super(context);
    }

    public DamagesWithSquashedFrogWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public DamagesWithSquashedFrogWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DamagesWithSquashedFrogWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCalback(DamagesWidget.DamagesCallback callback){
        this.damagesCallback = callback;
    }

    @Click
    protected void addDamage(){
        SquashedFrogActivity_.intent(getContext()).eventId(data.currentEventId).start();
    }

    @Override
    public Object getValue() {
        return DBHelper.getInstance().getDamagesByEventId(data.currentEventId+"");
    }

    @Override
    public void setData(DamagesWithSquashedFrogObject data) {
        this.data = data;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public String getProperty() {
        return data.jsonProperty;
    }

    @Override
    public DamagesWithSquashedFrogObject getStructure() {
        data.value = data.currentEventId+"";
        return data;
    }

}
