package server;




import com.google.inject.Inject;
import com.google.inject.Provider;

public class ServerImpl implements Server {

    ServerRunnable serverRunnable;
    public ServerImpl(Provider<ServerRunnable> provider) {
        serverRunnable = provider.get();
    }

    private final static String STATIC_FILES_LOCATION = "/";

    @Override
    public void stopServer() {
        //TODO Est-ce qu'il faut arrêter le serveur Jetty?
    }

    @Override
    public void startServer() {
        System.out.println("ServerRunnable = " + serverRunnable);
        Thread t = new Thread(serverRunnable);
        t.setDaemon(true);
        t.start();
    }

}