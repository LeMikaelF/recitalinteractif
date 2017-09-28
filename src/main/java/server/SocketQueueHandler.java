package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mikaël on 2016-10-27.
 */

@WebSocket
public class SocketQueueHandler {
    private static final Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    private static final ConcurrentHashMap<Session, Vote> clientCurrentVote = new ConcurrentHashMap<>();
    private static PollController pollController;
    private static int numTextonCourant;
    private static int numberOfLinks;
    private static Thread threadBroadcast;

    public static void setPollController(PollController pollController) {
        SocketQueueHandler.pollController = pollController;
    }

    public static void setBroadcastInfo(int numTextonCourant, int numberOfLinks) {
        System.out.println("Setting broadcast info \t numTexton :" + numTextonCourant + ", number of links : " + numberOfLinks);
        SocketQueueHandler.numTextonCourant = numTextonCourant;
        SocketQueueHandler.numberOfLinks = numberOfLinks;
    }

    private static Runnable broadcast() {
        return () -> {
            while (!Thread.interrupted()) {
                clients.forEach(session -> {
                    try {
                        session.getRemote().sendString(String.valueOf(numberOfLinks) + ";" + String.valueOf(numTextonCourant));
                    } catch (IOException e) {
                        System.out.println("Exception broadcasting...");
                        e.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        };
    }

    public static void startBroadcasting() {
        Thread threadBroadcast = new Thread(SocketQueueHandler::broadcast);
        threadBroadcast.setDaemon(true);
        threadBroadcast.start();
    }

    private static Vote getVoteLog(ControlCode cc) {
        Vote vote = Vote.NULL;
        switch (cc) {
            case APLUS:
                vote = Vote.A;
                break;
            case BPLUS:
                vote = Vote.B;
                break;
            case CPLUS:
                vote = Vote.C;
                break;
            case DPLUS:
                vote = Vote.D;
                break;
            case AMINUS:
            case BMINUS:
            case CMINUS:
            case DMINUS:
                vote = Vote.NULL;
                break;
            default:
                break;
        }
        return vote;
    }

    private static ControlCode getOppositeCcFromVote(Vote vote) {
        switch (vote) {
            case A:
                return ControlCode.AMINUS;
            case B:
                return ControlCode.BMINUS;
            case C:
                return ControlCode.CMINUS;
            case D:
                return ControlCode.DMINUS;
            case NULL:
                return ControlCode.BLANK;
        }
        return ControlCode.BLANK;
    }

    @SuppressWarnings("unused")
    @OnWebSocketMessage
    public void onMessage(Session session, String str) {
        ControlCode cc;
        //Test to see if the received message is a ControleCode.
        try {
            cc = ControlCode.valueOf(str);
            Vote toStore = getVoteLog(cc);
            System.out.println("Ajout du client et du vote suivant dans le Map : " + session.getRemoteAddress() + "\t" + toStore.toString());
            clientCurrentVote.put(session, toStore);
            if (!cc.equals(ControlCode.BLANK))
                System.out.println("Texte reçu du client : " + cc);

        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //Call a function to deal with non-ControlCode messages, or do nothing.
            System.out.println("Un message a été reçu : " + str);
            return;
        }

        try {
            pollController.queue.add(cc);
            System.out.println("Cc added to queue.");
        } catch (IllegalStateException e) {
            System.out.println("ControlCode queue is full.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        clients.remove(session);
        System.out.printf("Connection closed. StatusCode : %s \t Reason : %s \n", Integer.toString(statusCode), reason);
        System.out.println("Annulation du vote du client suite à la fermeture de la connection.");
        pollController.queue.add(ControlCode.UNREG);
        cancelVote(clientCurrentVote.get(session));
    }

    @SuppressWarnings("unused")
    @OnWebSocketConnect
    public void onConnect(Session session) {
        clients.add(session);
        System.out.println("Connecté à : " + session.getRemoteAddress().getAddress());
        pollController.queue.add(ControlCode.REG);

        //Envoyer un message au client
        try {
            session.getRemote().sendString("Connexion ouverte");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        pollController.queue.add(ControlCode.UNREG);
        clients.remove(session);
        System.out.println("Annulation du vote du client suite à une erreur de connection.");
        cancelVote(clientCurrentVote.get(session));
        System.out.println("Erreur WebSocket. Ceci est le contenu du Throwable : ");
        throwable.printStackTrace(System.err);
        if (throwable instanceof TimeoutException) {
            System.out.println("Timeout détecté.");
        }
    }

    private void cancelVote(Vote vote) {
        pollController.queue.add(getOppositeCcFromVote(vote));
    }

}
