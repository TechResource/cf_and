package flightpath.com.donglemodule.di;

import dagger.Module;
import dagger.Provides;
import flightpath.com.donglemodule.DongleDataHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-30.
 */
@Module
public class DongleModule {

    @DongleScope
    @Provides
    public DongleDataHelper dongleDataHelper(){
        return new DongleDataHelper();
    }

}
