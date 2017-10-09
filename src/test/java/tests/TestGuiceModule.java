package tests;

import builder.plugins.LtoPlugin;
import builder.plugins.StatisticsPlugin;
import com.google.inject.AbstractModule;

/**
 * Created by Mikaël on 2017-10-08.
 */
public class TestGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StatisticsPlugin.class).to(LtoPlugin.class);
    }
}
