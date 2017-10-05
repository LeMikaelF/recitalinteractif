package builder;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

/**
 * Created by Mikaël on 2017-10-04.
 */
public class BuilderGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BuilderVisContr.class).toInstance(new BuilderVisContr());
        bind(BuilderContr.class).toInstance(new BuilderContr());
        bind(EventBus.class).toInstance(new EventBus());
    }
}
