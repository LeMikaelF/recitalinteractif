package server;

import com.google.common.eventbus.Subscribe;
import events.TextonChangeEvent;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

/**
 * Created by Mikaël on 2017-10-02.
 */
public interface WebsocketHandler extends VoteCollector {
    @Subscribe
    void onTextonChangeEvent(TextonChangeEvent tce);
    @OnWebSocketMessage
    void onMessage(Session session, String str);
    @OnWebSocketClose
    void onClose(Session session, int statusCode, String reason);
    @OnWebSocketConnect
    void onConnect(Session session);
    @OnWebSocketError
    void onError(Session session, Throwable throwable);
    @Override
    void startBroadcasting();
    @Override
    void resetVotes();
}
