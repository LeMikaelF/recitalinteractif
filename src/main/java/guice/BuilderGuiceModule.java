package guice;

import builder.BuilderContr;
import builder.BuilderVisContr;
import builder.plugins.LtoPlugin;
import builder.plugins.StatisticsPlugin;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Created by Mikaël on 2017-10-04.
 */
public class BuilderGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BuilderVisContr.class).toInstance(new BuilderVisContr());
        bind(BuilderContr.class).toInstance(new BuilderContr());
        bind(EventBus.class).toInstance(new EventBus());

        //Declare statistics plugins for builder
        Multibinder<StatisticsPlugin> statisticsPluginMultibinder = Multibinder.newSetBinder(binder(), StatisticsPlugin.class);
        statisticsPluginMultibinder.addBinding().to(LtoPlugin.class);

        install(new CommonModule());
    }
}
