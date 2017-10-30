package server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import javax.servlet.DispatcherType;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;

/**
 * Created by Mikaël on 2017-10-01.
 */
public class ServerRunnableImpl implements ServerRunnable {
    private org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server();

    public ServerRunnableImpl() {
    }

    @Override
    public org.eclipse.jetty.server.Server getServer() {
        return server;
    }

    @Override
    public void run() {
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(80);
        server.addConnector(connector);

        try {
            URL homeDir = getClass().getResource("/public/index.html");
            URI homeUri = homeDir.toURI().resolve("./").normalize();

            ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
            context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
            ServletHolder holderPwd = new ServletHolder("default", WebjarDefaultServlet.class);
            holderPwd.setInitParameter("dirAllowed", "true");
            holderPwd.setInitParameter("resourceBase", homeUri.toString());
            context.addServlet(holderPwd, "/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}