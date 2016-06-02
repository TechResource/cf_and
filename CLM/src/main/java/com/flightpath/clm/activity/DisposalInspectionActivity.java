package com.flightpath.clm.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flightpath.clm.R;
import com.flightpath.clm.widgets.DisposalImageWidget;
import com.flightpath.clm.widgets.DisposalImageWidget_;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DisposalInspectionTable;
import com.flightpathcore.database.tables.EventTable;
import com.flightpathcore.database.tables.ItemsDamagedTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.objects.DisposalObject;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.objects.ListItem;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.utilities.Utils;
import com.flightpathcore.widgets.HeaderGridView;
import com.flightpathcore.widgets.InputWidget;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.InspectionsUtilities;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 09.05.2016.
 */
@EActivity(R.layout.activity_disposal_inspection)
public class DisposalInspectionActivity extends CLMBaseActivity implements DisposalImageWidget.DisposalImageCallback, HeaderFragment.HeaderCallback {

    public static final int REQUEST_TAKE_PHOTO = 535;

    @ViewById
    protected InputWidget registration;
    @ViewById
    protected Button addPhoto;
    @ViewById
    protected GridView imgContainer;
    @FragmentByTag
    protected HeaderFragment headerFragment;
    private DisposalImagesAdapter adapter;
    protected Uri photoFile = null;
    private DisposalObject disposalObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
        InspectionsUtilities.createImageLoaderInstance(getApplicationContext());

        if(savedInstanceState != null) {
            photoFile = savedInstanceState.getParcelable("photo_file");
            disposalObject = (DisposalObject) savedInstanceState.getSerializable("disposal_object");

        }
        if(disposalObject == null){
            disposalObject = new DisposalObject();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("photo_file", photoFile);
        outState.putSerializable("disposal_object", disposalObject);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            photoFile = savedInstanceState.getParcelable("photo_file");
            disposalObject = (DisposalObject) savedInstanceState.getSerializable("disposal_object");
            if(disposalObject != null && disposalObject.imagePaths.size() >= 8)
                addPhoto.setVisibility(View.GONE);
            else
                addPhoto.setVisibility(View.VISIBLE);

            if(disposalObject == null || disposalObject.imagePaths == null || disposalObject.imagePaths.size() == 0 || disposalObject.imagePaths.size() >= 8)
                headerFragment.setRightBtnEnabled(false);
            else headerFragment.setRightBtnEnabled(true);
        }
    }

    @AfterViews
    protected void init() {
        headerFragment.setViewType(HeaderFragment.ViewType.DISPOSAL_INSPECTION);
        headerFragment.setHeaderCallback(this);
        if(disposalObject == null || disposalObject.imagePaths == null || disposalObject.imagePaths.size() == 0 || disposalObject.imagePaths.size() >= 8)
            headerFragment.setRightBtnEnabled(false);
        else headerFragment.setRightBtnEnabled(true);

        adapter = new DisposalImagesAdapter();
        imgContainer.setAdapter(adapter);
        validate(false);
    }

    @Click
    protected void addPhoto(){
        if(disposalObject.imagePaths.size() >= 8){
            addPhoto.setVisibility(View.GONE);
        }else {
            takePhoto();
        }
    }

    protected void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = Uri.fromFile(Utils.createEmptyImageFile());
            } catch (IOException ex) {
                Toast.makeText(this, "Could not createEmptyImageFile", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                // takePictureIntent.putExtra("extraName", "extra");
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                Utils.decodeUri(this, photoFile, 400);
                disposalObject.imagePaths.add(photoFile.toString());
                validate(false);
                adapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRemove(int position) {
        disposalObject.imagePaths.remove(position);
        adapter.notifyDataSetChanged();
        validate(false);
    }

    @Override
    public void onHeaderLeftBtnClick() {
        finish();
    }

    @Override
    public void onHeaderRightBtnClick() {
        //save to db
        if(validate(true)) {
            disposalObject.registrationNumber = registration.getValue();
            DisposalInspectionTable dt = new DisposalInspectionTable();
            disposalObject.id = DBHelper.getInstance().insert(dt, dt.getContentValues(disposalObject));

            EventObject event = new EventObject();
            event.type = EventObject.EventType.DISPOSAL_INSPECTION;
            event.timestamp = Utilities.getTimestamp() + "";

            EventTable et = new EventTable();
            disposalObject.eventId = DBHelper.getInstance().insert(et, et.getContentValues(event));
            DBHelper.getInstance().updateOrInsert(dt, dt.getContentValues(disposalObject), disposalObject.id + "");

            event.customEventObject = new Gson().toJson(disposalObject);
            event.eventId = disposalObject.eventId;
            DBHelper.getInstance().updateOrInsert(et, et.getContentValues(event), event.eventId+"");

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean validate(boolean check) {
        boolean valid = true;

        if(registration.getValue().isEmpty()) {
            if(check)
                registration.setError(getString(R.string.field_required_error));
            valid = false;
        }
        if(disposalObject.imagePaths.size() >= 8)
            addPhoto.setVisibility(View.GONE);

        if(disposalObject.imagePaths.size() == 0) {
            addPhoto.setVisibility(View.VISIBLE);
            valid = false;
        }

        headerFragment.setRightBtnEnabled(valid);
        return valid;
    }

    @Override
    public void onMenuBtnClick() {

    }

    public class DisposalImagesAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return disposalObject.imagePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return disposalObject.getImage(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = DisposalImageWidget_.build(DisposalInspectionActivity.this);
            }
            ((DisposalImageWidget)convertView).setData(disposalObject.getImage(position), position, DisposalInspectionActivity.this);
            return convertView;
        }
    }


}
