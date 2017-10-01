package server;

import com.google.common.eventbus.Subscribe;
import events.TextonChangeEvent;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

/**
 * Created by Mikaël on 2017-09-29.
 */
@WebSocket
public class WebsocketHandler extends WebsocketHandlerBase {

    @Override
    @Subscribe
    public void onTextonChangeEvent(TextonChangeEvent tce) {
        setNumberOfLinks(tce.getGraph().getChildren(tce.getTexton().getNumTexton()).length);
        setNumTextonCourant(tce.getTexton().getNumTexton());
        resetVotes();
    }

    @Override
    @OnWebSocketMessage
    public void onMessage(Session session, String str) {
        Vote vote = Vote.valueOf(str);
        getClientsVotesMap().put(session, vote);
        updateProperties();
    }

    @Override
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        getClientsVotesMap().remove(session);
    }

    @Override
    @OnWebSocketConnect
    public void onConnect(Session session) {
        getClientsVotesMap().put(session, Vote.NULL);
    }

    @Override
    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        //Do nothing
    }

}
