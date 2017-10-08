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
    private final Provider<WebsocketHandler> provider;
    private Provider<WebSocketCreator> webSocketCreatorProvider;
    private org.eclipse.jetty.server.Server server;

    @Inject
    public ServerRunnableImpl(Provider<WebsocketHandler> provider) {
        this.provider = provider;
        this.webSocketCreatorProvider = webSocketCreatorProvider;
    }

    @Override
    public org.eclipse.jetty.server.Server getServer() {
        return server;
    }

    @Override
    public void run() {
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        server = new org.eclipse.jetty.server.Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(80);
        server.addConnector(connector);

        // The filesystem paths we will map
        String homePath = System.getProperty("user.home");
        String pwdPath = System.getProperty("user.dir");
        try {
            URL homeDir = getClass().getResource("/public/index.html");
            System.out.println("homeDir = " + homeDir);
            URI homeUri = homeDir.toURI().resolve("./").normalize();

            // Setup the basic application "context" for this application at "/"
            // This is also known as the handler tree (in jetty speak)
            ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
            context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
            ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
            holderPwd.setInitParameter("dirAllowed", "true");
            holderPwd.setInitParameter("resourceBase", homeUri.toString());
            context.addServlet(holderPwd, "/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}
