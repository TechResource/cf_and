package com.flightpathcore.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.flightpathcore.widgets.DrawingPanel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;

@EActivity(resName = "activity_signature")
public class SignatureActivity extends Activity {

    @ViewById
    protected DrawingPanel signView;
    @ViewById
    protected Button save, cancel;

    @AfterViews
    protected void init() {
        signView.requestFocus();
    }

    private void showPreview(String e) {
        byte[] decodedString = Base64.decode(e, Base64.DEFAULT);

        Intent intent = new Intent();
        intent.putExtra("sign", decodedString);
        if (getIntent().getExtras() != null) {
            intent.putExtra("extra_value", getIntent().getExtras().getString("extra_value"));
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    @Click
    protected void save(){
        Bitmap b = loadBitmapFromView(signView);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        showPreview(encodedImage);
    }

    @Click
    protected void cancel(){
        finish();
    }

}
