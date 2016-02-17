package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flightpathcore.objects.ItemsDamagedObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.DamagesObject;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EViewGroup(resName = "widget_inspection_damages")
public class DamagesWidget extends LinearLayout implements InspectionWidgetInterface<DamagesObject>{

    @ViewById
    protected Button addDamage;
    @ViewById
    protected LinearLayout damagesContainer;
    private DamagesCallback damagesCallback;
    protected Dialog damageDialog = null;
    private boolean damageDialogShouldBeOpen = false;
    private DamagesObject data;

    public DamagesWidget(Context context) {
        super(context);
    }

    public DamagesWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public DamagesWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DamagesWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCalback(DamagesCallback callback){
        this.damagesCallback = callback;
    }

    @Click
    protected void addDamage(){
        if(damagesCallback != null){
            damagesCallback.onNewDmg();
        }
    }

    public void addNewDamage(Uri photoFile){
        InputWidget et = InputWidget_.build(getContext());
        et.setData(new InputObject(null, getResources().getString(R.string.description_label), null, null, true, true));
        damageDialogShouldBeOpen = true;
        damageDialog = new AlertDialog.Builder(getContext(), R.style.BlueAlertDialog)
                .setView(et)
                .setPositiveButton(R.string.ok_label, (dialog, which) -> {
                    damageDialogShouldBeOpen = false;
                    ItemsDamagedObject item = new ItemsDamagedObject();
                    item.dmgDescription = et.getContentValue();
//                        item.eventId = ((InspectionAddActivity) getActivity()).getCurrentEventId();
                    item.imagePath = photoFile.toString();
//                        DBHelper.getHelper(getActivity()).createDamagedItem(item);

                    DamageWidget view = DamageWidget_.build(getContext());
                    view.setDamage(item);
                    view.dmgImg.setOnClickListener(v -> damagesCallback.showFullImg(item));
                    view.removeDmg.setOnClickListener(v -> onRemoveDamage(view));
                    damagesContainer.addView(view);
                })
                .setNegativeButton(R.string.cancel_label, (dialog1, which1) -> {
                    damageDialogShouldBeOpen = false;
                })
                .show();
    }

    private void onRemoveDamage(View viewToRemove) {
        // TODO remove dmg from db
        damagesContainer.removeView(viewToRemove);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setData(DamagesObject data) {
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

    public interface DamagesCallback{
        void onNewDmg();
        void showFullImg(ItemsDamagedObject damagedObject);
    }


}
