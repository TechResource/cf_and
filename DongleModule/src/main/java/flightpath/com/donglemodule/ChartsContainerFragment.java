package flightpath.com.donglemodule;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Button;

import com.flightpathcore.adapters.PagerAdapter;
import com.flightpathcore.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import eu.lighthouselabs.obd.reader.config.ObdConfig;
import eu.lighthouselabs.obd.reader.drawable.CoolantGaugeView;
import flightpath.com.donglemodule.ChartFragment;
import flightpath.com.donglemodule.ChartFragment_;
import flightpath.com.donglemodule.DongleDataHelper;
import flightpath.com.donglemodule.DonglePreferences;
import flightpath.com.donglemodule.GaugeFragment;
import flightpath.com.donglemodule.GaugeFragment_;
import flightpath.com.donglemodule.di.DIDongleModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-08.
 */
@EFragment(resName = "fragment_charts_container")
public class ChartsContainerFragment extends BaseFragment implements DongleDataHelper.DongleDataListener{

    @ViewById
    protected ViewPager chartsPager;
    @ViewById
    protected CoolantGaugeView coolantGauge;
    @ViewById
    protected Button disconnectDevice;
    @Inject
    protected DongleDataHelper dongleDataHelper;

    private PagerAdapter pagerAdapter;
    private List<String> fragments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIDongleModule.diDongleModule().injections().inject(this);
    }

    @AfterViews
    protected void init(){
        dongleDataHelper.addListener(this);
        fragments.clear();
        fragments.add("android:switcher:" + chartsPager.getId() + ":" + 0);
        fragments.add("android:switcher:" + chartsPager.getId() + ":" + 1);
        ChartFragment chartFragment = ChartFragment_.builder().build();
        GaugeFragment gaugeFragment = GaugeFragment_.builder().build();
        getChildFragmentManager().beginTransaction()
                .add(chartsPager.getId(), chartFragment, fragments.get(0))
                .add(chartsPager.getId(), gaugeFragment, fragments.get(1))
                .commit();
        pagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        chartsPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dongleDataHelper.removeListener(this);
    }

    @Override
    public void onDongleDataReceived(Map<String, String> dongleData) {
        try {
            String[] cools = dongleData.get(ObdConfig.COOLANT_TEMP).split(" ");
            int temp = Integer.parseInt(cools[0]);
            if ("F".equals(cools[1])) {
                temp = (temp - 32) * 5 / 9;
            }
            coolantGauge.setTemp(temp);
        } catch (Exception e) {

        }
    }

    @Click
    protected void disconnectDevice(){
        DonglePreferences.savePairedDevice(getContext(), null);
        dongleDataHelper.disconnectDevice();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
