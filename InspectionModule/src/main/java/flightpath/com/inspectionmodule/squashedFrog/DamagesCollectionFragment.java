package flightpath.com.inspectionmodule.squashedFrog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.CollectionDamagesTable;
import com.flightpathcore.database.tables.ItemsDamagedTable;
import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.utilities.Utils;
import com.flightpathcore.widgets.InputWidget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-07.
 */
@EFragment(resName = "fragment_damages_collection")
public class DamagesCollectionFragment extends BaseFragment {

    public static final int REQUEST_TAKE_PHOTO = 86;

    @ViewById
    protected InputWidget damageDescription;
    @ViewById
    protected GridView damagesContainer;
    @ViewById
    protected Button takePhoto, deleteBtn;
    @FragmentArg
    protected CollectionDamagesObject damagesCollection;

    private DamagesCollectionAdapter adapter = new DamagesCollectionAdapter();
    private Uri photoFile = null;
    private long currentEventId;
    public boolean deleted = false;

    @AfterViews
    protected void init(){
        Utilities.setOswaldTypeface(getResources().getAssets(), takePhoto, deleteBtn);
        damagesContainer.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(damagesCollection != null){
            this.damageDescription.setText(damagesCollection.description);
        }else{
            this.damageDescription.setText("");
        }
    }

    public void setData(CollectionDamagesObject damageCollection, long eventId){
        currentEventId = eventId;
//        this.damagesCollection = (CollectionDamagesObject) DBHelper.getInstance().get(new CollectionDamagesTable(), damageCollection.id+"");
        this.damagesCollection = damageCollection;
        if(damageDescription != null)
            this.damageDescription.setText((damageCollection.description == null ? "" : damageCollection.description));

        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Click
    protected void deleteBtn(){
        for (ItemsDamagedObject i : damagesCollection.damages){
            DBHelper.getInstance().removeDamagedItemById(i.id);
        }
        DBHelper.getInstance().remove(new CollectionDamagesTable(), damagesCollection.id+"");
        deleted = true;
        ((SquashedFrogActivity)getActivity()).onDamageDeleted();
        getActivity().onBackPressed();
    }

    @Click
    protected void takePhoto(){
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                Utils.decodeUri(getActivity(), photoFile, 400);
//                ((DamagesWidget)widgetsContainer.findViewWithTag(R.integer.view_tag_damages)).addNewDamage(photoFile);
                ItemsDamagedObject item = new ItemsDamagedObject();
                item.eventId = currentEventId;
                item.imagePath = photoFile.toString();
                item.isSent = 1;
                item.collectionId = damagesCollection.id;

                ItemsDamagedTable dmgTable = new ItemsDamagedTable();
                item.id = DBHelper.getInstance().insert(dmgTable, dmgTable.getContentValues(item));
                damagesCollection.damages.add(item);
                adapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        if(!deleted) {
            CollectionDamagesTable ct = new CollectionDamagesTable();
            damagesCollection.description = damageDescription.getValue();
            DBHelper.getInstance().updateOrInsert(ct, ct.getContentValues(damagesCollection), damagesCollection.id + "");
        }
        deleted = false;
        return false;
    }

    public class DamagesCollectionAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return damagesCollection.damages != null ? damagesCollection.damages.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return damagesCollection.damages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = SingleDamageWidget_.build(getContext());
            }
            ((SingleDamageWidget)convertView).setData(damagesCollection.damages.get(position));
            return convertView;
        }
    }
}
