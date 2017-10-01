package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import presentation.*;

import java.io.IOException;

/**
 * Created by Mikaël on 2017-09-29.
 */
public final class FxGuiceModule extends AbstractModule {
    @Override
    protected void configure() {

       /* bind(CommsManager.class).to(CommsManagerImpl.class).asEagerSingleton();
        bind(TabBordContr.class).to(TabBordContrImpl.class).asEagerSingleton();
        bind(VisContr.class).to(VisContrImpl.class).asEagerSingleton();*/


        try {
            CommsManagerImpl commsManager = new CommsManagerImpl();

            TabBordContr tabBordContr = new TabBordContrImpl();
            VisContr visContr = new VisContrImpl(() -> commsManager);

            bind(CommsManager.class).toInstance(commsManager);
            bind(TabBordContr.class).toInstance(tabBordContr);
            bind(VisContr.class).toInstance(visContr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //bind(EventBus.class).asEagerSingleton();
    }
}
