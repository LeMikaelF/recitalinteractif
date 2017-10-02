package server;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Created by Mikaël on 2017-10-02.
 */
public class ServerRunnableImplProvider implements Provider<ServerRunnableImpl> {
    @Inject
    Provider<WebsocketHandler> provider;



    @Override
    public ServerRunnableImpl get() {
        System.out.println("Ceci est le Provider<ServerRunnable>--------------------------------");
        return new ServerRunnableImpl(provider);
    }
}
