package server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.TextonChangeEvent;
import javafx.beans.property.IntegerProperty;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Mikaël on 2017-09-29.
 */
@WebSocket
public class WebsocketHandlerImpl implements WebsocketHandler {

    private final static String[] propNames = new String[]{"A", "B", "C", "D", "Enr"};
    protected static ConcurrentMap<String, IntegerProperty> properties = new ConcurrentHashMap<>();
    private static ConcurrentMap<Session, Vote> clientsVotesMap = new ConcurrentHashMap<>();
    private static AtomicInteger numberOfLinks = new AtomicInteger();
    private static AtomicInteger numTextonCourant = new AtomicInteger();
    private static Runnable broadcast = () -> {
        while (!Thread.interrupted()) {
            WebsocketHandlerImpl.getClientsVotesMap().keySet().forEach(session -> {
                try {
                    session.getRemote().sendString(new JSONObject().put("numLiens", WebsocketHandlerImpl.getNumberOfLinks()).put("textonCourant", WebsocketHandlerImpl.numTextonCourant.get()).toString());
                    Thread.sleep(1000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    @Inject
    EventBus eventBus;

    public WebsocketHandlerImpl() {
        System.out.println(eventBus);
    }

    private static void startBroadcastingImpl() {
        Thread t = new Thread(WebsocketHandlerImpl.broadcast);
        t.setDaemon(true);
        t.run();
    }

    private static void setNumTextonCourantImpl(int numTextonCourant) {
        WebsocketHandlerImpl.numTextonCourant.set(numTextonCourant);
    }

    protected static int getNumberOfLinks() {
        return numberOfLinks.get();
    }

    private static void setNumberOfLinks(int numberOfLinks) {
        WebsocketHandlerImpl.numberOfLinks.set(numberOfLinks);
    }

    public static ConcurrentMap<Session, Vote> getClientsVotesMap() {
        return WebsocketHandlerImpl.clientsVotesMap;
    }

    private void updateProperties() {
        //Calling this method each time to rebuild the complete properties is very wasteful, but economical.
        Arrays.stream(WebsocketHandlerImpl.propNames).forEach(
                s -> WebsocketHandlerImpl.properties.get(s).set(Math.toIntExact(WebsocketHandlerImpl.getClientsVotesMap().values().stream().filter(vote -> s.equals(vote.toString())).count()))
        );
    }

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

    @Override
    public void setNumTextonCourant(int numTextonCourant) {
        WebsocketHandlerImpl.setNumTextonCourantImpl(numTextonCourant);
    }

    @Override
    public void startBroadcasting() {
        WebsocketHandlerImpl.startBroadcastingImpl();
    }

    @Override
    public void resetVotes() {
        clientsVotesMap.values().clear();
    }

}
