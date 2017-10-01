package server;

import events.TextonChangeEvent;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Mikaël on 2017-09-29.
 */

public abstract class WebsocketHandlerBase {

    private final static String[] propNames = new String[]{"A", "B", "C", "D", "Enr"};
    protected static ConcurrentMap<String, IntegerProperty> properties = new ConcurrentHashMap<>();
    private static ConcurrentMap<Session, Vote> clientsVotesMap = new ConcurrentHashMap<>();
    private static AtomicInteger numberOfLinks = new AtomicInteger();
    private static AtomicInteger numTextonCourant = new AtomicInteger();
    private static Runnable broadcast = () -> {
        while (!Thread.interrupted()) {
            getClientsVotesMap().keySet().forEach(session -> {
                try {
                    session.getRemote().sendString(new JSONObject().put("numLiens", getNumberOfLinks()).put("textonCourant", getNumTextonCourant()).toString());
                    Thread.sleep(1000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    {
        for (int i = 0; i < getPropNames().length; i++) {
            properties.put(getPropNames()[i], new SimpleIntegerProperty());
        }
    }

    public static String[] getPropNames() {
        return propNames;
    }

    protected static int getNumberOfLinks() {
        return numberOfLinks.get();
    }

    protected static void setNumberOfLinks(int numberOfLinks) {

        WebsocketHandlerBase.numberOfLinks.set(numberOfLinks);
    }

    protected static int getNumTextonCourant() {
        return numTextonCourant.get();
    }

    protected static void setNumTextonCourant(int numTextonCourant) {
        WebsocketHandlerBase.numTextonCourant.set(numTextonCourant);
    }

    protected static ConcurrentMap<Session, Vote> getClientsVotesMap() {
        return clientsVotesMap;
    }

    public static void startBroadcasting() {
        Thread t = new Thread(broadcast);
        t.setDaemon(true);
        t.run();
    }

    public abstract void onTextonChangeEvent(TextonChangeEvent tce);

    protected static void updateProperties() {
        //Calling this method each time to rebuild the complete properties is very wasteful, but economical.
        Arrays.stream(propNames).forEach(
                s -> properties.get(s).set(Math.toIntExact(getClientsVotesMap().values().stream().filter(vote -> s.equals(vote.toString())).count()))
        );
    }

    protected void resetVotes() {
        clientsVotesMap.values().clear();
    }

    //Websocket methods must be annotated @Websocket
    abstract void onMessage(Session session, String str);

    abstract void onClose(Session session, int statusCode, String reason);

    abstract void onConnect(Session session);

    abstract void onError(Session session, Throwable throwable);
}
