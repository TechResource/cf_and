package flightpath.com.inspectionmodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flightpathcore.adapters.PagerAdapter;
import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.EventTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.objects.BaseWidgetObject;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.InspectionStructureResponse;
import com.flightpathcore.objects.InspectionWidgetTypes;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.SwipeableViewPager;
import com.flightpathcore.utilities.Utilities;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;
import flightpath.com.inspectionmodule.widgets.objects.DamagesObject;
import flightpath.com.inspectionmodule.widgets.objects.DamagesWithSquashedFrogObject;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;
import flightpath.com.inspectionmodule.widgets.objects.LooseItemsObject;
import flightpath.com.inspectionmodule.widgets.objects.SectionHeaderObject;
import flightpath.com.inspectionmodule.widgets.objects.SpinnerObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-11.
 */
@EFragment(resName = "fragment_inspection_container")
public class InspectionContainerFragment extends BaseFragment implements ViewPager.OnPageChangeListener, InspectionModuleInterfaces.InspectionContainerCallback,
        HeaderFragment.HeaderCallback, InspectionStep1Fragment.InspectionStep1Callback {

    @ViewById
    protected RelativeLayout btn1, btn2;
    @ViewById
    protected SwipeableViewPager pager;
    @ViewById
    protected FrameLayout select1, select2;
    @ViewById
    protected LinearLayout pagerStrip, addInspection;
    @ViewById
    protected TextView addInspectionBtn;

    protected HeaderFragment headerFragment;
    private List<String> fragments = new ArrayList<>();
    private PagerAdapter pagerAdapter;
    private InspectionStructureResponse inspectionStructure;
    private List<BaseWidgetObject> widgetStep1;
    private EventObject currentEventObject = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InspectionsUtilities.createImageLoaderInstance(getActivity().getApplicationContext());
    }

    @AfterViews
    protected void init() {
        if (currentEventObject == null) {
            String savedInpsEventId = SPHelper.getData(getContext(), SPHelper.SAVED_INSPECTION_EVENT_ID);
            if (savedInpsEventId != null && !savedInpsEventId.isEmpty()) {
                currentEventObject = (EventObject) DBHelper.getInstance().get(new EventTable(), savedInpsEventId);
            }
            if (currentEventObject == null) {
                currentEventObject = new EventObject();
                currentEventObject.driverId = ((UserObject) DBHelper.getInstance().getLast(new DriverTable())).driverId;
                currentEventObject.timestamp = Utilities.getTimestamp() + "";
                currentEventObject.type = EventObject.EventType.INSPECTION;
                currentEventObject.isSent = true;
                EventTable et = new EventTable();
                currentEventObject.eventId = DBHelper.getInstance().insert(et, et.getContentValues(currentEventObject));
            }
        }


        headerFragment = (HeaderFragment) getChildFragmentManager().findFragmentById(R.id.headerFragment);
        Utilities.setOswaldTypeface(getActivity().getAssets(), addInspectionBtn);

        String savedInspection = SPHelper.getData(getContext(), SPHelper.SAVED_INSPECTION);
        headerFragment.setViewType(HeaderFragment.ViewType.INSPECTION);
        headerFragment.setRightBtnText(getString(R.string.save_label));
        headerFragment.setLeftBtnText((savedInspection == null || savedInspection.isEmpty()) ? null : getString(R.string.delete_label));
        headerFragment.setHeaderCallback(this);

        fragments.clear();
        fragments.add("android:switcher:" + pager.getId() + ":" + 0);
        fragments.add("android:switcher:" + pager.getId() + ":" + 1);
        InspectionStep1Fragment step1Fragment = InspectionStep1Fragment_.builder().build();
        InspectionStep2Fragment step2Fragment = InspectionStep2Fragment_.builder().build();
        step1Fragment.setCallback(this);
        getChildFragmentManager().beginTransaction()
                .add(pager.getId(), step1Fragment, fragments.get(0))
                .add(pager.getId(), step2Fragment, fragments.get(1))
                .commit();
        pagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        pager.addOnPageChangeListener(this);
        pager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();
    }

    @Click
    protected void addInspection() {
        if (!checkValidity()) {
            return;
        }
        JSONObject json = new JSONObject();
        try {
            JSONObject step1 = ((InspectionStep1Fragment) pagerAdapter.getItem(0)).collectData(currentEventObject.eventId);
            if (pager.isSwipeAble()) {
                json = ((InspectionStep2Fragment) pagerAdapter.getItem(1)).collectData(step1);
            } else {
                json = step1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.d("Inspection", "all data: " + json.toString());

        currentEventObject.isSent = false;
        currentEventObject.customEventObject = json.toString();
        currentEventObject.timestamp = Utilities.getTimestamp() + "";
        EventTable et = new EventTable();
        DBHelper.getInstance().updateOrInsert(et, et.getContentValues(currentEventObject), currentEventObject.eventId + "");
        DBHelper.getInstance().markDamagesReadyToSend(currentEventObject.eventId);

        SPHelper.saveData(getContext(), SPHelper.SAVED_INSPECTION_EVENT_ID, null);
        SPHelper.saveData(getContext(), SPHelper.SAVED_INSPECTION, null);
        SynchronizationHelper.getInstance().updateCounter();
        ((InspectionModuleInterfaces.InspectionCompleteListener) getActivity()).onCompleteListener();
    }

    private boolean checkValidity() {
        boolean validity = false;
        validity = ((InspectionStep1Fragment) pagerAdapter.getItem(0)).validation();
        if (((InspectionStep2Fragment) pagerAdapter.getItem(1)).validation() && validity) {
            validity = true;
        }
        return validity;
    }

    @Click
    protected void btn1() {
        pager.setCurrentItem(0, true);
    }

    @Click
    protected void btn2() {
        pager.setCurrentItem(1, true);
    }

    public void buildInspection(InspectionStructureResponse inspectionStructure) {
        String savedInspection = SPHelper.getData(getContext(), SPHelper.SAVED_INSPECTION);
        if (savedInspection != null && !savedInspection.isEmpty()) {
            widgetStep1 = null;
            InspectionStructureResponse savedStructure = new Gson().fromJson(savedInspection, InspectionStructureResponse.class);
            if (savedStructure != null) {
                this.inspectionStructure = savedStructure;
            }
        } else {
            this.inspectionStructure = inspectionStructure;
        }
    }

    public void blockPager() {
        pagerStrip.setVisibility(View.GONE);
        pagerAdapter.blockPager();
        pagerAdapter.notifyDataSetChanged();
        pager.setSwipeAble(false);
    }

    public void unblockPager() {
        pagerStrip.setVisibility(View.VISIBLE);
        pagerAdapter.unblockPager();
        pagerAdapter.notifyDataSetChanged();
        pager.setSwipeAble(true);
        pager.setCurrentItem(1, true);
    }

    @Override
    public String getTitle() {
        return "Inspection";
    }

    @Override
    public boolean onBackPressed() {
        if (pager.getCurrentItem() == 1) {
            pager.setCurrentItem(0, true);
            return true;
        } else {
            return ((BaseFragment) pagerAdapter.getItem(0)).onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            select1.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
            select2.setBackgroundColor(getResources().getColor(R.color.dark_blue_color));
        } else if (position == 1) {
            select1.setBackgroundColor(getResources().getColor(R.color.dark_blue_color));
            select2.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public List<BaseWidgetObject> getWidgetsStep1() {
        if (widgetStep1 == null) {
            widgetStep1 = new ArrayList<>();

            for (BaseWidgetObject base : inspectionStructure.inspection) {
                if (base != null) {
                    if (base.type.equalsIgnoreCase(InspectionWidgetTypes.SPINNER)) {
                        widgetStep1.add(new SpinnerObject(base));
                    } else if (base.type.equalsIgnoreCase(InspectionWidgetTypes.SECTION_HEADER)) {
                        widgetStep1.add(new SectionHeaderObject(base));
                    } else if (base.type.equalsIgnoreCase(InspectionWidgetTypes.INPUT)) {
                        widgetStep1.add(new InputObject(base));
                    } else if (base.type.equalsIgnoreCase(InspectionWidgetTypes.CHECK_BOX)) {
                        widgetStep1.add(new CheckBoxObject(base));
                    } else if (base.type.equalsIgnoreCase(InspectionWidgetTypes.DAMAGES)) {
                        widgetStep1.add(new DamagesObject(base, currentEventObject.eventId));
                    } else if (base.type.equalsIgnoreCase(InspectionWidgetTypes.LOOSE_ITEMS)) {
                        widgetStep1.add(new LooseItemsObject(base));
                    } else if (base.type.equalsIgnoreCase(InspectionWidgetTypes.DAMAGES_WITH_SQUASHED_FROG)) {
                        widgetStep1.add(new DamagesWithSquashedFrogObject(base, currentEventObject.eventId));
                    }
                }
            }
        }
        return widgetStep1;
    }

    @Override
    public List<BaseWidgetObject> getWidgetStep2() {
        return inspectionStructure.widgetsStep2;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InspectionStep1Fragment.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            pagerAdapter.getItem(0).onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == InspectionStep2Fragment.REQUEST_SIGN_IN && resultCode == Activity.RESULT_OK) {
            pagerAdapter.getItem(1).onActivityResult(requestCode, resultCode, data);
        }
    }

    public HeaderFragment getHeader() {
        return headerFragment;
    }

    @Override
    public void onHeaderLeftBtnClick() {
        //delete saved inspection and clear view
        SPHelper.saveData(getContext(), SPHelper.SAVED_INSPECTION, null);
        SPHelper.saveData(getContext(), SPHelper.SAVED_INSPECTION_EVENT_ID, null);
        getActivity().finish();
    }

    @Override
    public void onHeaderRightBtnClick() {
        //save inspection
        inspectionStructure.inspection = ((InspectionStep1Fragment) pagerAdapter.getItem(0)).collectStructure();
        inspectionStructure.widgetsStep2 = ((InspectionStep2Fragment) pagerAdapter.getItem(1)).collectStructure();
        SPHelper.saveData(getContext(), SPHelper.SAVED_INSPECTION, new Gson().toJson(inspectionStructure));
        SPHelper.saveData(getContext(), SPHelper.SAVED_INSPECTION_EVENT_ID, currentEventObject.eventId + "");
        getActivity().finish();
    }

    @Override
    public void onMenuBtnClick() {

    }


    @Override
    public void onStep2Enable() {
        pager.setSwipeAble(true);
        pager.setCurrentItem(1, true);
        pagerStrip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStep2Disable() {
        pager.setSwipeAble(false);
        pager.setCurrentItem(0, true);
        pagerStrip.setVisibility(View.GONE);
    }

    @Override
    public void onJobChanged(JobObject selectedJob) {
        ((InspectionStep2Fragment) pagerAdapter.getItem(1)).onJobChanged(selectedJob);
    }
}
