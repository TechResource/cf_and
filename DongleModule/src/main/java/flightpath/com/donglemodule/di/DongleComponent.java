package flightpath.com.donglemodule.di;

import com.flightpathcore.base.BaseActivity;

import dagger.Component;
import flightpath.com.donglemodule.BTDevicePickerFragment;
import flightpath.com.donglemodule.ChartFragment;
import flightpath.com.donglemodule.ChartsContainerFragment;
import flightpath.com.donglemodule.DongleContainerFragment;
import flightpath.com.donglemodule.DongleDataHelper;
import flightpath.com.donglemodule.GaugeFragment;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-30.
 */
@Component(modules = DongleModule.class)
@DongleScope
public interface DongleComponent {
    DongleDataHelper dongleDataHelper();

    void inject(DongleContainerFragment fragment);
    void inject(BaseActivity activity);
    void inject(BTDevicePickerFragment fragment);
    void inject(ChartsContainerFragment fragment);
    void inject(ChartFragment fragment);
    void inject(GaugeFragment fragment);
}
