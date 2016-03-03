package flightpath.com.donglemodule;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.flightpathcore.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Map;

import javax.inject.Inject;

import eu.lighthouselabs.obd.reader.config.ObdConfig;
import flightpath.com.donglemodule.di.DIDongleModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-08.
 */
@EFragment(resName = "fragment_gauge")
public class GaugeFragment extends BaseFragment implements DongleDataHelper.DongleDataListener {

    private static final int CHART_UPDATE_PERIOD = 5 * 1000; //5 sec

    @ViewById
    protected TextView speedVal, rpmVal;
    @ViewById
    protected SpeedometerGaugeView speedometer, rpmmeter;
    @Inject
    protected DongleDataHelper dongleDataHelper;
    private long lastChartUpdate = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIDongleModule.diDongleModule().injections().inject(this);
    }

    @AfterViews
    protected void init() {
        dongleDataHelper.addListener(this);

        // configure value range and ticks
        speedometer.setMaxSpeed(240);
        speedometer.setMajorTickStep(30);
        speedometer.setMinorTicks(2);
        // // Configure value range colors
        speedometer.addColoredRange(30, 140, Color.GREEN);
        speedometer.addColoredRange(140, 180, Color.YELLOW);
        speedometer.addColoredRange(180, 400, Color.RED);
        // Add label converter
        speedometer.setLabelConverter((progress1, maxProgress2) -> String.valueOf((int) Math.round(progress1)));
        rpmmeter.setMaxSpeed(10000);
        rpmmeter.setMajorTickStep(1200);
        rpmmeter.setMinorTicks(80);
        // // Configure value range colors
        rpmmeter.addColoredRange(1200, 5600, Color.GREEN);
        rpmmeter.addColoredRange(5600, 7200, Color.YELLOW);
        rpmmeter.addColoredRange(7200, 12000, Color.RED);
        // Add label converter
        rpmmeter.setLabelConverter((progress, maxProgress1) -> String.valueOf((int) Math.round(progress)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dongleDataHelper.removeListener(this);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @UiThread
    @Override
    public void onDongleDataReceived(Map<String, String> dongleData) {
        if (needUpdate() && speedometer != null && dongleData != null) {
            speedometer.setSpeed(getMilesFromKilometres(getDecimalValue(dongleData.get(ObdConfig.SPEED))), true);
            speedVal.setText(getMilesFromKilometres(getDecimalValue(dongleData.get(ObdConfig.SPEED))) + "");
            rpmmeter.setSpeed(getDecimalValue(dongleData.get(ObdConfig.RPM)), true);
            rpmVal.setText(getDecimalValue(dongleData.get(ObdConfig.RPM)) + "");
        }
    }

    private boolean needUpdate() {
        long currentTime = System.currentTimeMillis();
        if (lastChartUpdate + CHART_UPDATE_PERIOD < currentTime) {
            lastChartUpdate = currentTime;
            return true;
        }
        return false;
    }

    private final static double kilometresInMile = 1.60934400;

    private int getMilesFromKilometres(int kilometres) {
        return (int) ((double) kilometres / kilometresInMile);
    }

    private int getDecimalValue(String value) {
        int decimalValue = 0;
        try {
            String[] val = value.split("\\s+");
            decimalValue = Integer.parseInt(val[0]);
        } catch (Exception e) {
            //value could be "--"
        }
        return decimalValue;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
