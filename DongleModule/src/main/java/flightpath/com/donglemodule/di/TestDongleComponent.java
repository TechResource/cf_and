package flightpath.com.donglemodule.di;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-30.
 */
@DongleScope
@Component(modules = TestDongleModule.class)
public interface TestDongleComponent extends DongleComponent {
}
