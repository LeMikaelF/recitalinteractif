package guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import io.JsonFileConnector;
import io.TextonFileIo;
import io.TextonIo;
import io.TextonIoFactory;
import presentation.*;
import server.*;

/**
 * Created by Mikaël on 2017-09-29.
 */
public final class FxGuiceModule extends AbstractModule {
    @Override
    protected void configure() {

        EventBus eventBus = new EventBus();
        bind(EventBus.class).toProvider(() -> eventBus);

        bind(ServerRunnable.class).toProvider(ServerRunnableImplProvider.class);
        bind(CommsManager.class).toProvider(CommsManagerImplProvider.class).asEagerSingleton();
        bind(VisContr.class).toProvider(VisContrImplProvider.class).asEagerSingleton();
        bind(TabBordContr.class).toProvider(TabBordContrImplProvider.class).asEagerSingleton();
        bind(Server.class).toProvider(ServerImplProvider.class).asEagerSingleton();
        bind(WebsocketHandler.class).toProvider(WebsocketHandlerImplProvider.class);

        Multibinder<VoteCollector> multibinder = Multibinder.newSetBinder(binder(), VoteCollector.class);
        multibinder.addBinding().to(WebsocketHandlerImpl.class);

        install(new CommonModule());
    }

}
