package flightpath.com.donglemodule.di;

import dagger.Module;
import dagger.Provides;
import flightpath.com.donglemodule.DongleDataHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-30.
 */
@DongleScope
@Module
public class TestDongleModule {
    private DongleDataHelper dongleDataHelper;

    public TestDongleModule(DongleDataHelper dongleDataHelper){
        this.dongleDataHelper = dongleDataHelper;
    }

    @DongleScope
    @Provides
    public DongleDataHelper dongleDataHelper(){
        return dongleDataHelper;
    }

}
