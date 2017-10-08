package server;

/**
 * Created by Mikaël on 2017-10-02.
 */
public interface ServerRunnable extends Runnable {
    org.eclipse.jetty.server.Server getServer();
    @Override
    void run();
}
