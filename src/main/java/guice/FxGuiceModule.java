package guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
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

        /*try {
            EventBus eventBus = new EventBus();
            bind(EventBus.class).toProvider(() -> eventBus);

            CommsManagerImpl commsManager = new CommsManagerImpl();

            TabBordContr tabBordContr = new TabBordContrImpl();
            VisContr visContr = new VisContrImpl(() -> commsManager);

            bind(CommsManager.class).toInstance(commsManager);
            bind(TabBordContr.class).toInstance(tabBordContr);
            bind(VisContr.class).toInstance(visContr);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
