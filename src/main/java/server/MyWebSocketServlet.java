package server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.jetty.websocket.servlet.*;

import javax.inject.Singleton;

/**
 * Created by Mikaël on 2017-10-02.
 */
@Singleton
public class MyWebSocketServlet extends WebSocketServlet {
    @Inject
    private
    Provider<WebsocketHandlerImpl> provider;
    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.setCreator((servletUpgradeRequest, servletUpgradeResponse) -> provider.get());
    }
}
