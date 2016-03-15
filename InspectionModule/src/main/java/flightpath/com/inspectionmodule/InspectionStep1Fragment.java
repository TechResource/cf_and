package flightpath.com.inspectionmodule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.utilities.Utils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.widgets.CheckBoxWidget;
import flightpath.com.inspectionmodule.widgets.CheckBoxWidget_;
import flightpath.com.inspectionmodule.widgets.DamageWidget;
import flightpath.com.inspectionmodule.widgets.DamagesWidget;
import flightpath.com.inspectionmodule.widgets.DamagesWidget_;
import flightpath.com.inspectionmodule.widgets.DamagesWithSquashedFrogWidget;
import flightpath.com.inspectionmodule.widgets.DamagesWithSquashedFrogWidget_;
import flightpath.com.inspectionmodule.widgets.InputWidget;
import flightpath.com.inspectionmodule.widgets.InputWidget_;
import flightpath.com.inspectionmodule.widgets.LooseItemsWidget;
import flightpath.com.inspectionmodule.widgets.LooseItemsWidget_;
import flightpath.com.inspectionmodule.widgets.SectionHeaderWidget;
import flightpath.com.inspectionmodule.widgets.SectionHeaderWidget_;
import flightpath.com.inspectionmodule.widgets.SpinnerWidget;
import flightpath.com.inspectionmodule.widgets.SpinnerWidget_;
import flightpath.com.inspectionmodule.widgets.InspectionWidgetInterface;
import com.flightpathcore.objects.BaseWidgetObject;
import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;
import flightpath.com.inspectionmodule.widgets.objects.DamagesObject;
import flightpath.com.inspectionmodule.widgets.objects.DamagesWithSquashedFrogObject;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;
import flightpath.com.inspectionmodule.widgets.objects.LooseItemsObject;
import flightpath.com.inspectionmodule.widgets.objects.SectionHeaderObject;
import flightpath.com.inspectionmodule.widgets.objects.SpinnerObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EFragment(resName = "fragment_inspection_step1")
public class InspectionStep1Fragment extends BaseFragment implements SpinnerWidget.SpinnerCallback, DamagesWidget.DamagesCallback {

    public static final int REQUEST_TAKE_PHOTO = 85;

    @ViewById
    protected LinearLayout widgetsContainer;
    @ViewById
    protected ImageView fullImg;
    @ViewById
    protected EditText dmgDescription;
    @ViewById
    protected RelativeLayout fullImgContainer;

    private List<Integer> allTags = new ArrayList<>();
    @InstanceState
    protected Uri photoFile = null;
    private InspectionStep1Callback callback;

    @AfterViews
    protected void init(){
        buildView(((InspectionModuleInterfaces.InspectionContainerCallback) getParentFragment()).getWidgetsStep1());
    }

    public void setCallback(InspectionStep1Callback callback){
        this.callback = callback;
    }

    private void buildView(List<BaseWidgetObject> widgets) {
        widgetsContainer.removeAllViews();
        for (BaseWidgetObject widget : widgets){
            if(widget instanceof SectionHeaderObject){
                SectionHeaderWidget sh = SectionHeaderWidget_.build(new ContextThemeWrapper(getContext(), R.style.Inspection_SectionHeader),null,0);
                sh.setData((SectionHeaderObject)widget);
                widgetsContainer.addView(sh);
            }else if(widget instanceof InputObject){
                InputWidget iw = InputWidget_.build(getContext());
                iw.setData((InputObject) widget);
                iw.setTag(widget.getViewTag());
                allTags.add(widget.getViewTag());
                widgetsContainer.addView(iw);
            }else if(widget instanceof SpinnerObject){
                SpinnerWidget sw = SpinnerWidget_.build(getContext() );
                sw.setData((SpinnerObject) widget);
                sw.setCallback(this);
                sw.setTag(widget.getViewTag());
                allTags.add(widget.getViewTag());
                widgetsContainer.addView(sw);
            }else if(widget instanceof CheckBoxObject){
                CheckBoxWidget cbw = CheckBoxWidget_.build(getContext());
                cbw.setData((CheckBoxObject) widget);
                if(((CheckBoxObject) widget).isPagerBlocker){
                    cbw.setOnCheckedChange((buttonView, isChecked) -> {
                        if(isChecked){
                            callback.onStep2Enable();
                        }else{
                            callback.onStep2Disable();
                        }
                    });
                }
                widgetsContainer.addView(cbw);
            }else if(widget instanceof DamagesObject){
                DamagesWidget dw = DamagesWidget_.build(getContext());
                dw.setData((DamagesObject) widget);
                dw.setTag(R.integer.view_tag_damages);
                dw.setCalback(this);
                widgetsContainer.addView(dw);
            }else if(widget instanceof LooseItemsObject){
                LooseItemsWidget liw = LooseItemsWidget_.build(getContext()/*new ContextThemeWrapper(getContext(), R.style.Inspection_Select), null,0*/);
                liw.setData((LooseItemsObject) widget);
                liw.setTag(R.integer.view_tag_loose_items);
                widgetsContainer.addView(liw);
            }else if(widget instanceof DamagesWithSquashedFrogObject){
                DamagesWithSquashedFrogWidget dwsfw = DamagesWithSquashedFrogWidget_.build(getContext());
                dwsfw.setData((DamagesWithSquashedFrogObject)widget);
                dwsfw.setTag(R.integer.view_tag_damages);
                dwsfw.setCalback(this);
                widgetsContainer.addView(dwsfw);
            }
        }
    }

    public JSONObject collectData(long eventId) throws JSONException {
        JSONObject json = new JSONObject();
        for (int i=0;i<widgetsContainer.getChildCount();i++){
            InspectionWidgetInterface w = (InspectionWidgetInterface)widgetsContainer.getChildAt(i);
            if(w.getProperty() != null) {
                if(w instanceof DamagesWidget){
                    JSONArray jAr = new JSONArray();
                    List<ItemsDamagedObject> arr = (List<ItemsDamagedObject>) w.getValue();
                    for (ItemsDamagedObject o : arr) {
                        jAr.put(o.getJson());
                    }
                    json.put(w.getProperty(), jAr);
                    try {
                        json.accumulate("damagedItemsCount", ((List) w.getValue()).size());
                    }catch (Exception e){
                        e.printStackTrace();
                        json.accumulate("damagedItemsCount", 0);
                    }
                }else if(w instanceof DamagesWithSquashedFrogWidget){
                    JSONArray jAr = new JSONArray();
                    List<ItemsDamagedObject> arr = (List<ItemsDamagedObject>) w.getValue();
                    for (ItemsDamagedObject o : arr) {
                        jAr.put(o.getJson());
                    }
                    json.put(w.getProperty(), jAr);
                    try {
                        json.accumulate("damagedItemsCount", ((List) w.getValue()).size());
                    }catch (Exception e){
                        e.printStackTrace();
                        json.accumulate("damagedItemsCount", 0);
                    }

                    JSONArray jArExtra = new JSONArray();
                    List<CollectionDamagesObject> arrExtra = DBHelper.getInstance().getCollectionsByEventId(eventId+"");
                    for (CollectionDamagesObject o : arrExtra) {
                        jArExtra.put(o.getJson());
                    }
                    json.put("collections", jArExtra);
                }else {
                    json.accumulate(w.getProperty(), w.getValue());
                }
            }
        }
        Log.d("Inspection", "step1 data :" + json.toString());
        return json;
    }

    public List<BaseWidgetObject> collectStructure(){
        List<BaseWidgetObject> structure = new ArrayList<>();
        for (int i=0;i<widgetsContainer.getChildCount();i++){
            InspectionWidgetInterface w = (InspectionWidgetInterface)widgetsContainer.getChildAt(i);
            structure.add(w.getStructure());
        }

        return structure;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onJobSelected(JobObject job) {
        String value = null;
        InspectionWidgetInterface widget = null;
        for (Integer tag : allTags) {
            widget = (InspectionWidgetInterface) widgetsContainer.findViewWithTag(tag);
            if (widget != null) {
                value = getJobValueByViewTag(job, tag);
                widget.setValue(value);
            }
        }
        if(widgetsContainer.findViewWithTag(R.integer.view_tag_loose_items) != null){
            ((LooseItemsWidget)widgetsContainer.findViewWithTag(R.integer.view_tag_loose_items))
                    .setJobData(job.selectedLooseItems, job.looseItems);
        }
        if(callback != null){
            callback.onJobChanged(job);
        }
    }

    private String getJobValueByViewTag(JobObject job, Integer viewTag){
        if(viewTag == R.integer.view_tag_registration) {
            if(job.vehicle != null){
                return job.vehicle.registration;
            }
        }else if(viewTag == R.integer.view_tag_manufacturer){
            if(job.vehicle != null) {
                return job.vehicle.manufacturer.name;
            }
        }else if(viewTag == R.integer.view_tag_model){
            if(job.vehicle != null) {
                return job.vehicle.model.name;
            }
        }else if(viewTag == R.integer.view_tag_loan_duration){
            return job.loan;
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                if(photoFile == null){
                    photoFile = new Uri.Builder().build();
                }
                Utils.decodeUri(getActivity(), photoFile, 400);
//                ((DamagesWidget)widgetsContainer.findViewWithTag(R.integer.view_tag_damages)).addNewDamage(photoFile);
                for (int i=0;i<widgetsContainer.getChildCount();i++){
                    if(widgetsContainer.getChildAt(i) instanceof DamagesWidget){
                        ((DamagesWidget)widgetsContainer.getChildAt(i)).addNewDamage(photoFile);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewDmg() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = Uri.fromFile(Utils.createEmptyImageFile());
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Could not createEmptyImageFile", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                // takePictureIntent.putExtra("extraName", "extra");
                getActivity().startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void showFullImg(ItemsDamagedObject damagedObject) {
        ImageLoader.getInstance().displayImage(damagedObject.imagePath, fullImg);
        dmgDescription.setText(damagedObject.dmgDescription);
        fullImgContainer.setVisibility(View.VISIBLE);
    }

    public boolean onHeaderLeftBtnClick() {
        return onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        if(fullImgContainer.getVisibility() == View.VISIBLE) {
            fullImgContainer.setVisibility(View.GONE);
            return true;
        }else{
            return false;
        }
    }

    public boolean validation() {
        boolean validity = true;
        boolean singleValidity;
        InspectionWidgetInterface w;
        for (int i=0;i<widgetsContainer.getChildCount();i++){
            w = (InspectionWidgetInterface) widgetsContainer.getChildAt(i);
            singleValidity = w.isValid();
            if(validity && !singleValidity){
                validity = false;
            }
        }
        return validity;
    }

    public interface InspectionStep1Callback{
        void onStep2Enable();
        void onStep2Disable();
        void onJobChanged(JobObject selectedJob);
    }
}
