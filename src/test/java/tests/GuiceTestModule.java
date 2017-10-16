package tests;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import io.JsonFileConnector;
import io.TextonIo;
import io.TextonIoFactory;
import util.TextonOverlayCanvas;
import util.TextonOverlayCanvasOnlyText;

/**
 * Created by Mikaël on 2017-10-11.
 */
public class GuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(TextonIo.class, JsonFileConnector.class).build(TextonIoFactory.class));
        bind(EventBus.class).in(Singleton.class);
    }
}
