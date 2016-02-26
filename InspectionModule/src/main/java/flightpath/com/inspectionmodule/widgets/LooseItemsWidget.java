package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.flightpathcore.objects.jobs.LooseItem;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.LooseItemsObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EViewGroup(resName = "widget_inspection_loose_items")
public class LooseItemsWidget extends FrameLayout implements InspectionWidgetInterface<LooseItemsObject>, DialogInterface.OnMultiChoiceClickListener {

    @ViewById
    protected Button looseItems;
    private LooseItemsObject data;

    public LooseItemsWidget(Context context) {
        super(context);
    }

    public LooseItemsWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LooseItemsWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LooseItemsWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init() {
        looseItems.setOnClickListener(v -> showLooseItemsDialog());
    }

    private void showLooseItemsDialog() {
        Utilities.styleAlertDialog(new AlertDialog.Builder(getContext(), R.style.BlueAlertDialog)
                .setTitle("Loose Items")
                .setMultiChoiceItems(data.getLooseItemsAsArray(), data.selectedLooseItems, this)
                .setPositiveButton(R.string.ok_label, (dialog, which) -> buildBtnText())
                .setNegativeButton(R.string.cancel_label, null)
                .show());
    }

    @Override
    public Object getValue() {
        return getLooseItemsAsString();
    }

    @Override
    public void setData(LooseItemsObject data) {
        this.data = data;
        if (data.looseItems != null) {
            buildBtnText();
        }
    }

    private void buildBtnText() {
        boolean allSelected = true;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.looseItems.size(); i++) {
            if (!data.selectedLooseItems[i]) {
                allSelected = false;
            } else {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(data.looseItems.get(i).name);
            }
        }
        if (allSelected) {
            looseItems.setText(getResources().getString(R.string.all_items_selected_label) + " (" + data.looseItems.size() + ")");
        } else {
            if (sb.toString().isEmpty()) {
                looseItems.setText("No items selected");
            } else {
                looseItems.setText(sb.toString());
            }
        }
    }

    private String getLooseItemsAsString(){
        if(looseItems != null){
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<data.selectedLooseItems.length;i++){
                if(data.selectedLooseItems[i]){
                    if(sb.length() > 0){
                        sb.append(", ");
                    }
                    sb.append(data.looseItems.get(i).name);
                }
            }

            return sb.toString();
        }else{
            return "";
        }
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
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        data.selectedLooseItems[which] = isChecked;
    }

    @Override
    public LooseItemsObject getStructure() {
        return data;
    }

    public void setJobData(boolean[] selectedLooseItems, List<LooseItem> looseItems) {
        if(!checkLooseItemsAreEqual(data.selectedLooseItems, data.looseItems, selectedLooseItems, looseItems)) {
            if (selectedLooseItems == null) {
                selectedLooseItems = new boolean[looseItems.size()];
                for (int i = 0; i < looseItems.size(); i++) {
                    selectedLooseItems[i] = true;
                }
            }
            data.selectedLooseItems = selectedLooseItems;
            data.looseItems = looseItems;
        }
        if (data.looseItems != null) {
            buildBtnText();
        }
    }

    private boolean checkLooseItemsAreEqual(boolean[] selectedLooseItems, List<LooseItem> looseItems, boolean[] selectedLooseItems2, List<LooseItem> looseItems2) {
        try {
            if(looseItems.size() != looseItems2.size()){
                return false;
            }
            for (int i=0;i<looseItems.size();i++){
                if(looseItems.get(i).id != looseItems2.get(i).id){
                    return false;
                }
                if(selectedLooseItems2 != null) {
                    if (selectedLooseItems[i] != selectedLooseItems2[i]) {
                        return false;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
