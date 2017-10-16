package guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import presentation.*;
import server.*;

/**
 * Created by Mikaël on 2017-09-29.
 */
public final class FxGuiceModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(EventBus.class).in(Singleton.class);
        bind(ServerRunnable.class).to(ServerRunnableImpl.class).in(Singleton.class);
        bind(CommsManager.class).to(CommsManagerImpl.class).in(Singleton.class);
        bind(VisContr.class).to(VisContrImpl.class).in(Singleton.class);
        bind(TabBordContr.class).to(TabBordContrImpl.class).in(Singleton.class);
        bind(Server.class).toProvider(ServerImplProvider.class).in(Singleton.class);

        Multibinder<VoteCollector> multibinder = Multibinder.newSetBinder(binder(), VoteCollector.class);
        multibinder.addBinding().to(WebsocketHandlerImpl.class);

        install(new CommonModule());
    }

}
