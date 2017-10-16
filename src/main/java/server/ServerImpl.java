package server;

import com.google.inject.Provider;

public class ServerImpl implements Server {

    private static final String STATIC_FILES_LOCATION = "/";
    ServerRunnable serverRunnable;
    private Thread threadServer;

    public ServerImpl(Provider<ServerRunnable> provider) {
        serverRunnable = provider.get();
    }

    @Override
    public void stopServer() throws Exception {
        serverRunnable.getServer().stop();
    }

    @Override
    public void startServer() {
        threadServer = new Thread(serverRunnable);
        threadServer.setDaemon(true);
        threadServer.start();
    }

}