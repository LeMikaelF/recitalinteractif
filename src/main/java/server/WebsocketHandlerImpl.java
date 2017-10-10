package server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import events.TextonChangeEvent;
import javafx.beans.property.IntegerProperty;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-09-29.
 */
@Singleton
@WebSocket
public class WebsocketHandlerImpl implements WebsocketHandler {

    private static final String[] propNames = {"A", "B", "C", "D", "Enr"};
    private static ConcurrentMap<String, IntegerProperty> properties = new ConcurrentHashMap<>();
    private static ConcurrentMap<Session, Vote> clientsVotesMap = new ConcurrentHashMap<>();
    private static AtomicInteger numberOfLinks = new AtomicInteger();
    private static AtomicInteger numTextonCourant = new AtomicInteger();
    private static Runnable broadcast = () -> {
        while (!Thread.interrupted()) {
            //TODO Ne pas broadcaster à toutes les secondes, pour sauver sur la bande passante.
            WebsocketHandlerImpl.getClientsVotesMap().keySet().forEach(session -> {
                try {
                    //TODO Builder json with Jackson rather than org.json
                    session.getRemote().sendString(new JSONObject().put("numLiens", WebsocketHandlerImpl.getNumberOfLinks()).put("textonCourant", WebsocketHandlerImpl.numTextonCourant.get()).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    };

    @Inject
    private EventBus eventBus;

    public WebsocketHandlerImpl() {
    }

    private static void startBroadcastingImpl() {
        Thread t = new Thread(WebsocketHandlerImpl.broadcast);
        t.setDaemon(true);
        t.start();
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
        List<Integer> voteList = Stream.of(Vote.values())
                .mapToInt(vote -> (int) clientsVotesMap.values().stream().filter(vote1 -> !vote1.equals(Vote.NULL))
                        .filter(vote::equals).count()).boxed().collect(Collectors.toList());

        eventBus.post(new VoteChangeEvent(properties.get("Enr").get(), voteList, this));
    }

    @Override
    @Subscribe
    public void onTextonChangeEvent(TextonChangeEvent tce) {
        setNumberOfLinks(tce.getGraph().getChildren(tce.getTexton().getNumTexton()).size());
        setNumTextonCourant(tce.getTexton().getNumTexton());
        resetVotes();
    }

    @Override
    @OnWebSocketMessage
    public void onMessage(Session session, String str) {
        System.out.println("Message reçu d'un client websocket : " + str);
        Vote vote = Vote.valueOf(str);
        getClientsVotesMap().put(session, vote);

        Vote[] voteArrayFull = Vote.values();
        Vote[] voteArrayOnlyVotes = new Vote[voteArrayFull.length - 1];
        System.arraycopy(voteArrayFull, 0, voteArrayOnlyVotes, 0, voteArrayFull.length - 1);
        List<Integer> votes = Arrays.stream(voteArrayOnlyVotes)
                .mapToInt(value -> new Long(getClientsVotesMap().values().stream().filter(value::equals)
                        .count()).intValue()).boxed().collect(Collectors.toList());

        eventBus.post(new VoteChangeEvent(getClientsVotesMap().keySet().size(), votes, this));
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
