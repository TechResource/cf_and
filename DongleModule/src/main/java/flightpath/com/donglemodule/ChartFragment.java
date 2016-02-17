package flightpath.com.donglemodule;

import android.os.Bundle;
import android.widget.TextView;

import com.flightpathcore.base.BaseFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
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
    protected TextView vehicleSpeed, engineRpm;
    @ViewById
    protected LineChart speedChart, rpmChart;
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
        chartsMap.put(ObdConfig.SPEED, speedChart);
        chartsMap.put(ObdConfig.RPM, rpmChart);
        chartsMap.get(ObdConfig.SPEED).setDescription("[mph]");
        chartsMap.get(ObdConfig.RPM).setDescription("[RPM]");
        lineDataMap.put(ObdConfig.SPEED, new LineData());
        lineDataMap.put(ObdConfig.RPM, new LineData());
        lineDataSetsMap.put(ObdConfig.SPEED, new LineDataSet(horizontalValuesMap.get(ObdConfig.SPEED), ObdConfig.SPEED));
        lineDataSetsMap.put(ObdConfig.RPM, new LineDataSet(horizontalValuesMap.get(ObdConfig.RPM), ObdConfig.RPM));

        for (int i = SPEED_PERIODS_NUMBER; i > 0; i-=5) {
            xValues.add(i + ".s");
        }
        lineDataSetMap.put(ObdConfig.SPEED, new LineDataSet(horizontalValuesMap.get(ObdConfig.SPEED), ObdConfig.SPEED));
        lineDataSetMap.put(ObdConfig.RPM, new LineDataSet(horizontalValuesMap.get(ObdConfig.RPM), ObdConfig.RPM));
        lineDataSetsArraysMap.put(ObdConfig.SPEED, new ArrayList<>());
        lineDataSetsArraysMap.put(ObdConfig.RPM, new ArrayList<>());
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
        if(needUpdate() && dongleData != null) {
            setData(dongleData.get(ObdConfig.RPM), ObdConfig.RPM);
            setData(dongleData.get(ObdConfig.SPEED), ObdConfig.SPEED);
        }
    }

    @UiThread
    protected void setData(String value, String cmd) {
        if(!viewReady){
            return;
        }
        int decimalValue = 0;
        if (horizontalValuesMap.get(cmd).size() >= SPEED_PERIODS_NUMBER/5) {
            horizontalValuesMap.get(cmd).remove(0);
            for (Entry entry : horizontalValuesMap.get(cmd)) {
                entry.setXIndex(entry.getXIndex() - 1);
            }
        }
        if (cmd.equals(ObdConfig.SPEED))
            decimalValue = getMilesFromKilometres(getDecimalValue(value));
        else
            decimalValue = getDecimalValue(value);

        Entry c1e1 = new Entry(decimalValue, horizontalValuesMap.get(cmd).size());
        horizontalValuesMap.get(cmd).add(c1e1);
        lineDataSetsMap.put(cmd, new LineDataSet(horizontalValuesMap.get(cmd), cmd));
        lineDataSetsArraysMap.get(cmd).clear();
        lineDataSetsArraysMap.get(cmd).add(lineDataSetsMap.get(cmd));
        lineDataMap.put(cmd, new LineData(xValues, lineDataSetsArraysMap.get(cmd)));
        chartsMap.get(cmd).setData(lineDataMap.get(cmd));
        chartsMap.get(cmd).invalidate(); // refresh
        if (cmd.equals(ObdConfig.SPEED)) {
            vehicleSpeed.setText(decimalValue + "");
        } else if (cmd.equals(ObdConfig.RPM)) {
            engineRpm.setText(decimalValue + "");
        }
    }

    private boolean needUpdate() {
        long currentTime = System.currentTimeMillis();
        if(lastChartUpdate + CHART_UPDATE_PERIOD < currentTime ){
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
