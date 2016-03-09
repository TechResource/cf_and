package flightpath.com.inspectionmodule.squashedFrog;

import android.view.View;
import android.widget.ImageView;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.objects.CollectionDamagesObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.R;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-07.
 */
@EFragment(resName = "fragment_squashed_frog_exterior")
public class SquashedFrogFragment extends BaseFragment {

    @ViewById
    protected ImageView squashedFrogImage;
    @ViewById
    protected SquashedFrogPanelView squashedFrogPanel;
    @FragmentArg
    protected CollectionDamagesObject.CollectionType type;
    @FragmentArg
    protected Long eventId;

    private List<CollectionDamagesObject> collections;

    @AfterViews
    protected void init() {
        if(collections == null) {
            collections = DBHelper.getInstance().getCollectionsByEventId(eventId + "");
            for (CollectionDamagesObject c : collections){
                c.damages = DBHelper.getInstance().getDamagesByCollectionId(c.id+"");
            }
            switch (type) {
                case INTERIOR:
                    squashedFrogImage.setImageResource(R.drawable.squashed_frog_interior);
                    squashedFrogPanel.setData(type, (SquashedFrogActivity) getActivity(), eventId, collections);
                    break;
                case EXTERIOR:
                    squashedFrogImage.setImageResource(R.drawable.squashed_frog_exterior);
                    squashedFrogPanel.setData(type, (SquashedFrogActivity) getActivity(), eventId, collections);
                    break;
                case TYRES:
                    squashedFrogImage.setImageDrawable(null);
                    squashedFrogPanel.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public List<CollectionDamagesObject> getCollections(){
        switch (type) {
            case INTERIOR:
            case EXTERIOR:
                return squashedFrogPanel.getCollection();
            case TYRES:
                //TODO
                break;
        }
        return new ArrayList<>();
    }

    @Override
    public String getTitle() {
        switch (type) {
            case INTERIOR:
                return "Interior Condition";
            case EXTERIOR:
                return "Exterior Condition";
            case TYRES:
                return "Tread Depths";
        }
        return "Exterior Condition";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    public void refreshDamages() {
        collections = DBHelper.getInstance().getCollectionsByEventId(eventId + "");
        for (CollectionDamagesObject c : collections){
            c.damages = DBHelper.getInstance().getDamagesByCollectionId(c.id+"");
        }
        squashedFrogPanel.refresh(collections);
    }
}
