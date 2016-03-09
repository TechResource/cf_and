package flightpath.com.inspectionmodule.squashedFrog;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.CollectionDamagesTable;
import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.widgets.InputWidget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-09.
 */
@EFragment(resName = "fragment_tread_depths")
public class TreadDepthsFragment extends BaseFragment {

    @ViewById
    protected InputWidget spareET, driverBackET, passengerBackET, driverFrontET, passengerFrontET;
    @ViewById
    protected CheckBox dualTyresCB;
    @FragmentArg
    protected Long eventId;

    private CollectionDamagesObject damagesCollection;

    @AfterViews
    protected void init(){
        List<CollectionDamagesObject> collections = DBHelper.getInstance().getCollectionsByEventId(eventId+"");
        for (CollectionDamagesObject c : collections){
            if(c.collectionType == CollectionDamagesObject.CollectionType.TYRES){
                damagesCollection = c;
                break;
            }
        }
        if(damagesCollection != null) {
            spareET.setText(getSimpleText(damagesCollection.spare));
            driverBackET.setText(getSimpleText(damagesCollection.driverBack));
            passengerBackET.setText(getSimpleText(damagesCollection.passengerBack));
            driverFrontET.setText(getSimpleText(damagesCollection.driverFront));
            passengerFrontET.setText(getSimpleText(damagesCollection.passengerFront));
            dualTyresCB.setChecked(damagesCollection.dualTyres);
        }else{
            damagesCollection = new CollectionDamagesObject();
            damagesCollection.collectionType = CollectionDamagesObject.CollectionType.TYRES;
        }
        spareET.setOnFocusChangeListener((v, hasFocus) -> onFocusChange(v, hasFocus));
        driverBackET.setOnFocusChangeListener((v, hasFocus) -> onFocusChange(v, hasFocus));
        driverFrontET.setOnFocusChangeListener((v, hasFocus) -> onFocusChange(v, hasFocus));
        passengerFrontET.setOnFocusChangeListener((v, hasFocus) -> onFocusChange(v, hasFocus));
        passengerBackET.setOnFocusChangeListener((v, hasFocus) -> onFocusChange(v, hasFocus));
    }

    private String getSimpleText(String t){
        if(t != null && !t.isEmpty()){
            t+="mm";
        }
        return t;
    }

    public void onFocusChange(View view, boolean hasFocus){
        if (hasFocus) {
            ((EditText) view).setText(((EditText) view).getText().toString().replace("mm", ""));
        } else {
            if(((EditText) view).getText().toString().replace("mm","").length() > 0)
                ((EditText) view).setText(((EditText) view).getText().toString() + "mm");
        }
        ((EditText)view).setSelection(((EditText) view).getText().length());
    }

    public void save(){
        CollectionDamagesTable ct = new CollectionDamagesTable();
        damagesCollection.spare = spareET.getValue().replace("mm", "");
        damagesCollection.driverFront = driverFrontET.getValue().replace("mm", "");
        damagesCollection.driverBack = driverBackET.getValue().replace("mm", "");
        damagesCollection.passengerFront = passengerFrontET.getValue().replace("mm", "");
        damagesCollection.passengerBack = passengerBackET.getValue().replace("mm", "");
        damagesCollection.dualTyres = dualTyresCB.isChecked();
        damagesCollection.eventId = eventId;
        DBHelper.getInstance().updateOrInsert(ct, ct.getContentValues(damagesCollection), damagesCollection.id+"");
    }

    @Override
    public String getTitle() {
        return "Tread Depths";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
