package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import flightpath.com.inspectionmodule.widgets.objects.SignatureObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-15.
 */
@EViewGroup(resName = "widget_inspection_signature")
public class SignatureWidget extends LinearLayout implements InspectionWidgetInterface<SignatureObject> {

    @ViewById
    protected Button signatureBtn;
    @ViewById
    protected ImageView signatureIV;
    private SignatureCallback callback;
    private SignatureObject data;

    public SignatureWidget(Context context) {
        super(context);
    }

    public SignatureWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignatureWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SignatureWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCallback(SignatureCallback callback){
        this.callback = callback;
    }

    @Click
    protected void signatureBtn(){
        if(callback != null){
            callback.onSignatureClick((String)getTag());
        }
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getProperty() {
        return data.jsonProperty;
    }

    @Override
    public void setData(SignatureObject data) {
        this.data = data;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void setValue(String value) {

    }

    public void setSignature(Bitmap signature){
        signatureIV.setImageBitmap(signature);
    }

    public interface SignatureCallback{
        void onSignatureClick(String viewTag);
    }
}
