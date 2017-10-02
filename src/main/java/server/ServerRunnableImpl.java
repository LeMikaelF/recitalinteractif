package server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by Mikaël on 2017-10-01.
 */
public class ServerRunnableImpl implements ServerRunnable {
    private final Provider<WebsocketHandler> provider;

    @Inject
    public ServerRunnableImpl(Provider<WebsocketHandler> provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        //System.setProperty("org.eclipse.jetty.LEVEL","INFO");
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(5555);



/*
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setResourceBase("/");
        context.setContextPath("/");

        server.setHandler(context);
        HandlerList handlers = new HandlerList();
        handlers.addBean(context);

        ServletHolder holder = new ServletHolder("default", DefaultServlet.class);
        holder.setInitParameter("resourceBuse", "/public/");
        holder.setInitParameter("dirAllowed", "true");
        holder.setInitParameter("pathInfoOnly", "true");
        context.addServlet(holder, "/C:\\Users\\Mikaël\\IdeaProjects\\Présentation\\src\\main\\resources\\public");

        WebSocketHandler wsh = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebsocketHandler.class);
            }
        };
        handlers.addBean(wsh);
        server.setHandler(handlers);
*/

        try {
            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}
