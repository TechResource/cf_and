package flightpath.com.donglemodule;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.utilities.Utilities;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import eu.lighthouselabs.obd.reader.config.ObdConfig;
import flightpath.com.donglemodule.di.DIDongleModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-13.
 */
@EFragment(resName = "fragment_chart")
public class ChartFragment extends BaseFragment implements DongleDataHelper.DongleDataListener {

    private static final int SPEED_PERIODS_NUMBER = 60;
    private static final int CHART_UPDATE_PERIOD = 5 * 1000; //5 sec

    @Inject
    protected DongleDataHelper dongleDataHelper;
    @ViewById
    protected TextView vehicleSpeed, engineRpm, /*fuelEconomy,*/ fuelEconomy2;
    @ViewById
    protected LineChart speedChart, rpmChart, /*economyFuelChart,*/ economyFuelChart2;
    private ArrayList<String> xValues = new ArrayList<>();
    private Map<String, LineDataSet> lineDataSetMap = new HashMap<>();
    private Map<String, ArrayList<Entry>> horizontalValuesMap = new HashMap<>();
    private Map<String, LineChart> chartsMap = new HashMap<>();
    private Map<String, LineData> lineDataMap = new HashMap<>();
    private Map<String, LineDataSet> lineDataSetsMap = new HashMap<>();
    private Map<String, ArrayList<LineDataSet>> lineDataSetsArraysMap = new HashMap<>();
    private boolean viewReady = false;
    private long lastChartUpdate = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIDongleModule.diDongleModule().injections().inject(this);
    }

    @AfterViews
    protected void init() {
        dongleDataHelper.addListener(this);
        horizontalValuesMap.put(ObdConfig.SPEED, new ArrayList<>());
        horizontalValuesMap.put(ObdConfig.RPM, new ArrayList<>());
//        horizontalValuesMap.put(ObdConfig.FUEL_ECON, new ArrayList<>());
        horizontalValuesMap.put(ObdConfig.FUEL_ECON, new ArrayList<>());
        chartsMap.put(ObdConfig.SPEED, speedChart);
        chartsMap.put(ObdConfig.RPM, rpmChart);
//        chartsMap.put(ObdConfig.FUEL_ECON, economyFuelChart);
        chartsMap.put(ObdConfig.FUEL_ECON, economyFuelChart2);
        chartsMap.get(ObdConfig.SPEED).setDescription("[mph]");
        chartsMap.get(ObdConfig.RPM).setDescription("[RPM]");
//        chartsMap.get(ObdConfig.FUEL_ECON).setDescription("[mpg]");
        chartsMap.get(ObdConfig.FUEL_ECON).setDescription("[mpg]");
        lineDataMap.put(ObdConfig.SPEED, new LineData());
        lineDataMap.put(ObdConfig.RPM, new LineData());
//        lineDataMap.put(ObdConfig.FUEL_ECON, new LineData());
        lineDataMap.put(ObdConfig.FUEL_ECON, new LineData());
        lineDataSetsMap.put(ObdConfig.SPEED, new LineDataSet(horizontalValuesMap.get(ObdConfig.SPEED), ObdConfig.SPEED));
        lineDataSetsMap.put(ObdConfig.RPM, new LineDataSet(horizontalValuesMap.get(ObdConfig.RPM), ObdConfig.RPM));
//        lineDataSetsMap.put(ObdConfig.FUEL_ECON, new LineDataSet(horizontalValuesMap.get(ObdConfig.FUEL_ECON), ObdConfig.FUEL_ECON));
        lineDataSetsMap.put(ObdConfig.FUEL_ECON, new LineDataSet(horizontalValuesMap.get(ObdConfig.FUEL_ECON2), ObdConfig.FUEL_ECON2));

        for (int i = SPEED_PERIODS_NUMBER; i > 0; i -= 5) {
            xValues.add(i + ".s");
        }
        lineDataSetMap.put(ObdConfig.SPEED, new LineDataSet(horizontalValuesMap.get(ObdConfig.SPEED), ObdConfig.SPEED));
        lineDataSetMap.put(ObdConfig.RPM, new LineDataSet(horizontalValuesMap.get(ObdConfig.RPM), ObdConfig.RPM));
//        lineDataSetMap.put(ObdConfig.FUEL_ECON, new LineDataSet(horizontalValuesMap.get(ObdConfig.FUEL_ECON), ObdConfig.FUEL_ECON));
        lineDataSetMap.put(ObdConfig.FUEL_ECON, new LineDataSet(horizontalValuesMap.get(ObdConfig.FUEL_ECON), ObdConfig.FUEL_ECON));
        lineDataSetsArraysMap.put(ObdConfig.SPEED, new ArrayList<>());
        lineDataSetsArraysMap.put(ObdConfig.RPM, new ArrayList<>());
//        lineDataSetsArraysMap.put(ObdConfig.FUEL_ECON, new ArrayList<>());
        lineDataSetsArraysMap.put(ObdConfig.FUEL_ECON, new ArrayList<>());
        viewReady = true;
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

    @Override
    public void onDongleDataReceived(Map<String, String> dongleData) {
        if (needUpdate() && dongleData != null) {
            setData(dongleData.get(ObdConfig.RPM), ObdConfig.RPM);
            setData(dongleData.get(ObdConfig.SPEED), ObdConfig.SPEED);
//            setData(dongleData.get(ObdConfig.FUEL_ECON), ObdConfig.FUEL_ECON);
            setData(dongleData.get(ObdConfig.FUEL_ECON), ObdConfig.FUEL_ECON);
            Log.d("DONGLE_DATA", new Gson().toJson(dongleData));
        }
    }

    @UiThread
    protected void setData(String value, String cmd) {
        if (!viewReady) {
            return;
        }
        double decimalValue = 0f;

        if (horizontalValuesMap.get(cmd).size() >= SPEED_PERIODS_NUMBER / 5) {
            horizontalValuesMap.get(cmd).remove(0);
            for (Entry entry : horizontalValuesMap.get(cmd)) {
                entry.setXIndex(entry.getXIndex() - 1);
            }
        }
        decimalValue = getDecimalValue(value);

        Entry c1e1 = new Entry((float) decimalValue, horizontalValuesMap.get(cmd).size());
        horizontalValuesMap.get(cmd).add(c1e1);
        lineDataSetsMap.put(cmd, new LineDataSet(horizontalValuesMap.get(cmd), cmd));
        lineDataSetsArraysMap.get(cmd).clear();
        lineDataSetsArraysMap.get(cmd).add(lineDataSetsMap.get(cmd));
        lineDataMap.put(cmd, new LineData(xValues, lineDataSetsArraysMap.get(cmd)));
        chartsMap.get(cmd).setData(lineDataMap.get(cmd));
        chartsMap.get(cmd).invalidate(); // refresh
        if (cmd.equals(ObdConfig.SPEED)) {
            vehicleSpeed.setText(Locale.getDefault().getLanguage().equalsIgnoreCase("pl") ? Utilities.MPHtoKMH(decimalValue)+" km/h" : value);
        } else if (cmd.equals(ObdConfig.RPM)) {
            engineRpm.setText(value);
//        } else if (cmd.equals(ObdConfig.FUEL_ECON)) {
//            decimalValue = 235.214583 / decimalValue;
//            fuelEconomy.setText(Locale.getDefault().getLanguage().equalsIgnoreCase("pl") ? String.format("%.1f %s", decimalValue,"kml") : value);
        }else if (cmd.equals(ObdConfig.FUEL_ECON)) {
            decimalValue = 235.214583 / decimalValue ;
            fuelEconomy2.setText(Locale.getDefault().getLanguage().equalsIgnoreCase("pl") ? String.format("%.1f %s", decimalValue,"l/100km") : value);
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

    private double getDecimalValue(String value) {
        double decimalValue = 0;
        try {
            String[] val = value.replace(",",".").split("\\s+");
            decimalValue = Double.parseDouble(val[0]);
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
