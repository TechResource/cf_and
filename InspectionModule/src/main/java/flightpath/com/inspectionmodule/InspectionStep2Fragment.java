package flightpath.com.inspectionmodule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.base.SignatureActivity_;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.jobs.LegalWordObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.widgets.CheckBoxWidget;
import flightpath.com.inspectionmodule.widgets.CheckBoxWidget_;
import flightpath.com.inspectionmodule.widgets.DamagesWidget;
import flightpath.com.inspectionmodule.widgets.DateTimeWidget;
import flightpath.com.inspectionmodule.widgets.DateTimeWidget_;
import flightpath.com.inspectionmodule.widgets.DateWidget;
import flightpath.com.inspectionmodule.widgets.DateWidget_;
import flightpath.com.inspectionmodule.widgets.InputWidget;
import flightpath.com.inspectionmodule.widgets.InputWidget_;
import flightpath.com.inspectionmodule.widgets.SignatureWidget;
import flightpath.com.inspectionmodule.widgets.SignatureWidget_;
import flightpath.com.inspectionmodule.widgets.InspectionWidgetInterface;
import com.flightpathcore.objects.BaseWidgetObject;
import com.flightpathcore.utilities.Utils;

import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;
import flightpath.com.inspectionmodule.widgets.objects.SignatureObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EFragment(resName = "fragment_inspection_step2")
public class InspectionStep2Fragment extends BaseFragment implements SignatureWidget.SignatureCallback {

    public static final int REQUEST_SIGN_IN = 2000;

    @ViewById
    protected LinearLayout step2Container;
    private List<BaseWidgetObject> widgets;
    private Integer signatureCounter = 1;

    @AfterViews
    protected void init() {
        buildStep(((InspectionModuleInterfaces.InspectionContainerCallback) getParentFragment()).getWidgetStep2());
    }

    public void buildStep(List<BaseWidgetObject> widgets) {
        this.widgets = widgets;

    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void onJobChanged(JobObject selectedJob) {
        step2Container.removeAllViews();
        if (selectedJob.legalWords != null) {
            for (LegalWordObject l : selectedJob.legalWords) {
                if (l.fieldType.equalsIgnoreCase("signature")) {
                    SignatureWidget sw = SignatureWidget_.build(getContext());
                    sw.setData(new SignatureObject());
                    sw.setTag(R.integer.view_tag_signature + "_" + signatureCounter);
                    signatureCounter++;
                    sw.setCallback(this);
                    step2Container.addView(sw);
                } else if (l.fieldType.equalsIgnoreCase("date")) {
                    DateWidget dw = DateWidget_.build(getContext());
                    dw.setData(new InputObject(null, l.placeholder, null, null, false, false));
                    step2Container.addView(dw);
                } else if (l.fieldType.equalsIgnoreCase("date_time")) {
                    DateTimeWidget dtw = DateTimeWidget_.build(getContext());
                    dtw.setData(new InputObject(null, l.placeholder, null, null, false, false));
                    step2Container.addView(dtw);
                } else if (l.fieldType.equalsIgnoreCase("email") || l.fieldType.equalsIgnoreCase("name")) {
                    InputWidget iw = InputWidget_.build(getContext());
                    iw.setData(new InputObject(null, l.placeholder, null, 0, false, true));
                    step2Container.addView(iw);
                }
            }

        }
        if(widgets != null){
            for (BaseWidgetObject w : widgets){
                if(w instanceof CheckBoxObject){
                    CheckBoxWidget cbw = CheckBoxWidget_.build(getContext());
                    cbw.setData((CheckBoxObject) w);
                    step2Container.addView(cbw);
                }
            }
        }
    }

    public JSONObject collectData(JSONObject currentJson) throws JSONException {
        for (int i = 0; i < step2Container.getChildCount(); i++) {
            InspectionWidgetInterface w = (InspectionWidgetInterface) step2Container.getChildAt(i);
            if (w.getProperty() != null) {
                currentJson.accumulate(w.getProperty(), w.getValue());
            }
        }
        currentJson.accumulate("termsImage", getImageAsBase64());
        Log.d("Inspection", "step2 data :" + currentJson.toString());
        return currentJson;
    }

    private String getImageAsBase64() {
        if (getView().findFocus() != null) {
            getView().findFocus().clearFocus();
        }
        Bitmap b = Utils.getBitmapFromView(step2Container);
        return Utils.getBase64StringFromBitmap(b);
    }

    @Override
    public void onSignatureClick(String viewTag) {
        SignatureActivity_.intent(getActivity()).extra("extra_value", viewTag).startForResult(REQUEST_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_IN) {
            byte[] byteArray = data.getByteArrayExtra("sign");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Log.d("TEST", data.getStringExtra("extra_value"));
            ((SignatureWidget) step2Container.findViewWithTag(data.getStringExtra("extra_value"))).setSignature(bmp);
        }
    }

    public boolean validation() {
        boolean validity = true;
        boolean singleValidity;
        InspectionWidgetInterface w;
        for (int i=0;i<step2Container.getChildCount();i++){
            w = (InspectionWidgetInterface) step2Container.getChildAt(i);
            singleValidity = w.isValid();
            if(validity && !singleValidity){
                validity = false;
            }
        }
        return validity;
    }

    public List<BaseWidgetObject> collectStructure() {
        List<BaseWidgetObject> structure = new ArrayList<>();
        for (int i = 0; i < step2Container.getChildCount(); i++) {
            InspectionWidgetInterface w = (InspectionWidgetInterface) step2Container.getChildAt(i);
            structure.add(w.getStructure());
        }
        return structure;
    }
}
