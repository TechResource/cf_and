package flightpath.com.inspectionmodule.squashedFrog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.util.Log;

import com.flightpathcore.adapters.PagerAdapter;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.utilities.SwipeableViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.InspectionsUtilities;
import flightpath.com.inspectionmodule.R;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-07.
 */
@EActivity(resName = "activity_squashed_frog")
public class SquashedFrogActivity extends BaseActivity implements SquashedFrogPanelView.SquashedFrogCallback {

    @FragmentByTag
    protected HeaderFragment headerFragment;
    @ViewById
    protected SwipeableViewPager pager;
    @ViewById
    protected PagerTabStrip pagerTabStrip;
    @Extra
    protected long eventId;

    private List<String> fragments;
    private PagerAdapter adapter = null;
    private DamagesCollectionFragment damagesCollectionFragment;
    private boolean dmgCollFrShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InspectionsUtilities.createImageLoaderInstance(this);
    }

    @AfterViews
    protected void init() {
        if (adapter == null) {
            headerFragment.setViewType(HeaderFragment.ViewType.CLEAR);

            pager.setOffscreenPageLimit(3);
            fragments = new ArrayList<>();
            fragments.add("android:switcher:" + pager.getId() + ":" + 0);
            fragments.add("android:switcher:" + pager.getId() + ":" + 1);
            fragments.add("android:switcher:" + pager.getId() + ":" + 2);

            if (getSupportFragmentManager().findFragmentByTag(fragments.get(0)) == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(pager.getId(), SquashedFrogFragment_.builder().type(CollectionDamagesObject.CollectionType.EXTERIOR).eventId(eventId).build(), fragments.get(0))
                        .add(pager.getId(), SquashedFrogFragment_.builder().type(CollectionDamagesObject.CollectionType.INTERIOR).eventId(eventId).build(), fragments.get(1))
                        .add(pager.getId(), TreadDepthsFragment_.builder().eventId(eventId).build(), fragments.get(2))
                        .commit();
            }

            adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
            pager.setAdapter(adapter);
            pager.setSwipeAble(true);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (dmgCollFrShowing) {
            if (!damagesCollectionFragment.onBackPressed()) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom_fade, R.anim.slide_out_bottom_fade)
                        .hide(damagesCollectionFragment)
                        .commit();
                dmgCollFrShowing = false;
            }
            return;
        }
        ((TreadDepthsFragment)adapter.getItem(2)).save();

        JSONObject json = new JSONObject();
        JSONArray jAr = new JSONArray();
        try {
            List<ItemsDamagedObject> arr = DBHelper.getInstance().getDamagesByEventId(eventId + "");
            for (ItemsDamagedObject o : arr) {
                jAr.put(o.getJson());
            }

            json.put("damagedItems", jAr);
            json.accumulate("damagedItemsCount", ((List) DBHelper.getInstance().getDamagesByEventId(eventId+"")).size());

            JSONArray jArExtra = new JSONArray();
            List<CollectionDamagesObject> arrExtra = DBHelper.getInstance().getCollectionsByEventId(eventId+"");
            for (CollectionDamagesObject o : arrExtra) {
                jArExtra.put(o.getJson());
            }
            json.put("collections", jArExtra);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("SqusgedFrog json", json.toString());


        super.onBackPressed();
    }

    @Override
    public void showDmgsDetails(CollectionDamagesObject damageCollection) {
        if (damagesCollectionFragment == null) {
            damagesCollectionFragment = DamagesCollectionFragment_.builder().damagesCollection(damageCollection).build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, damagesCollectionFragment, "damagesCollectionFragment")
                    .setCustomAnimations(R.anim.slide_in_bottom_fade, R.anim.slide_out_bottom_fade)
                    .show(damagesCollectionFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom_fade, R.anim.slide_out_bottom_fade)
                    .show(damagesCollectionFragment)
                    .commit();
        }
        damagesCollectionFragment.setData(damageCollection, eventId);
        dmgCollFrShowing = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DamagesCollectionFragment.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (damagesCollectionFragment != null) {
                damagesCollectionFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void onDamageDeleted() {
        ((SquashedFrogFragment) adapter.getItem(0)).refreshDamages();
        ((SquashedFrogFragment) adapter.getItem(1)).refreshDamages();
    }
}

