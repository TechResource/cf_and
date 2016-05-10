package com.flightpath.clm.widgets;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flightpath.clm.R;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.utilities.Utilities;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 09.05.2016.
 */
@EViewGroup(R.layout.widget_disposal_image)
public class DisposalImageWidget extends RelativeLayout {

    @ViewById
    protected ImageView img;
    @ViewById
    protected TextView removeDmg;
    private int position;
    private DisposalImageCallback callback;

    public DisposalImageWidget(Context context) {
        super(context);
    }

    public DisposalImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisposalImageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setData(String photoPath, int position, DisposalImageCallback callback) {
        ImageLoader.getInstance().displayImage(photoPath, img);
        this.position = position;
        this.callback = callback;
    }

    @Click
    protected void removeDmg(){
        Utilities.styleAlertDialog(
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.remove_image_text)
                        .setPositiveButton(R.string.yes_label, (dialog, which) -> callback.onRemove(position))
                        .setNegativeButton(R.string.no_label, null)
                        .show());
    }

    public interface DisposalImageCallback{
        void onRemove(int position);
    }
}
