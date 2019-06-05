package guice;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import io.JsonFileConnector;
import io.TextonIo;
import io.TextonIoFactory;

/**
 * Created by Mikaël on 2017-10-07.
 */
class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(TextonIo.class, JsonFileConnector.class).build(TextonIoFactory.class));
    }
}
