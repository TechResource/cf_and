package flightpath.com.donglemodule.di;

import com.flightpathcore.base.BaseActivity;

import flightpath.com.donglemodule.BTDevicePickerFragment;
import flightpath.com.donglemodule.ChartFragment;
import flightpath.com.donglemodule.ChartsContainerFragment;
import flightpath.com.donglemodule.DongleContainerFragment;
import flightpath.com.donglemodule.GaugeFragment;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-30.
 */
public class DIDongleModule {
    private static DIDongleModule diDongleModule = new DIDongleModule();
    private Injections injection = new Injections();
    public DongleComponent dongleComponent;

    public static DIDongleModule diDongleModule() {
        return diDongleModule;
    }

    public DIDongleModule(){
        dongleComponent = DaggerDongleComponent.builder().dongleModule(new DongleModule()).build();
    }

    public void setTestComponent(TestDongleComponent testDongleComponent){
        this.dongleComponent = testDongleComponent;
    }

    public Injections injections(){
        return injection;
    }

    public class Injections{
        public void inject(DongleContainerFragment fragment){
            dongleComponent.inject(fragment);
        }

        public void inject(BaseActivity activity){
            dongleComponent.inject(activity);
        }

        public void inject(BTDevicePickerFragment fragment) {
            dongleComponent.inject(fragment);
        }

        public void inject(ChartsContainerFragment fragment) {
            dongleComponent.inject(fragment);
        }

        public void inject(ChartFragment fragment) {
            dongleComponent.inject(fragment);
        }

        public void inject(GaugeFragment fragment) {
            dongleComponent.inject(fragment);
        }
    }
}
