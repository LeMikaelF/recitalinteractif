package server;

import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import util.PropLoader;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created by Mikaël on 2017-10-01.
 */
public class ServerRunnableImpl implements ServerRunnable {
    private final org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server();

    public ServerRunnableImpl() {
    }

    @Override
    public org.eclipse.jetty.server.Server getServer() {
        return server;
    }

    @Override
    public void run() {
        System.setProperty("org.eclipse.jetty.LEVEL", "DEBUG");

        ServerConnector connector = new ServerConnector(server);
        int port = Integer.parseInt(PropLoader.getMap().get("port"));
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.setInitParameter("dirAllowed", "true");
        context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        ServletHolder holderPwd = new ServletHolder("default", WebjarDefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed", "true");
        holderPwd.setInitParameter("resourceBase", this.getClass().getResource("/public").toExternalForm());
        context.addServlet(holderPwd, "/");
        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}
