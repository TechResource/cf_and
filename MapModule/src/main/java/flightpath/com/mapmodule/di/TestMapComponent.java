package flightpath.com.mapmodule.di;

import dagger.Component;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-25.
 */
@MapScope
@Component(modules = TestMapModule.class)
public interface TestMapComponent extends MapComponent {

}
