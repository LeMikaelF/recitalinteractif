package server;




import com.google.inject.Inject;
import com.google.inject.Provider;

public class ServerImpl implements Server {

    ServerRunnable serverRunnable;
    private Thread threadServer;

    public ServerImpl(Provider<ServerRunnable> provider) {
        serverRunnable = provider.get();
    }

    private static final String STATIC_FILES_LOCATION = "/";

    @Override
    public void stopServer() throws Exception {
        serverRunnable.getServer().stop();
    }

    @Override
    public void startServer() {
        System.out.println("ServerRunnable = " + serverRunnable);
        threadServer = new Thread(serverRunnable);
        threadServer.setDaemon(true);
        threadServer.start();
    }

}