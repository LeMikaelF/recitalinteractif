package server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.TextonChangeEvent;
import events.VoteChangeEvent;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Mikaël on 2017-09-29.
 */
@WebSocket
public class WebsocketHandlerImpl implements WebsocketHandler {

    private static final AtomicBoolean broadcasting = new AtomicBoolean(false);
    private static final AtomicReference<BroadcastInfo> broadcastInfo = new AtomicReference<>();
    private static ConcurrentMap<Session, Vote> clientsVotesMap = new ConcurrentHashMap<>();
    @Inject
    private EventBus eventBus;

    public WebsocketHandlerImpl() {
    }

    private static void broadcast(Session session) {
        if (broadcasting.get())
            try {
                session.getRemote().sendString(new ObjectMapper().writeValueAsString(broadcastInfo.get()));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private static void broadcastAll() {
        if (broadcasting.get())
            WebsocketHandlerImpl.getClientsVotesMap().keySet().forEach(WebsocketHandlerImpl::broadcast);
    }

    private static ConcurrentMap<Session, Vote> getClientsVotesMap() {
        return WebsocketHandlerImpl.clientsVotesMap;
    }

    public void setBroadcastInfo(int textonCourant, int numLiens, List<String> texts) {
        broadcastInfo.set(new BroadcastInfo(numLiens, textonCourant, texts));
    }

    @Override
    @Subscribe
    public void onTextonChangeEvent(TextonChangeEvent tce) {
        setBroadcastInfo(tce.getTexton().getNumTexton(),
                tce.getGraph().getChildren(tce.getTexton().getNumTexton()).size(),
                tce.getGraph().getChildren(tce.getTexton().getNumTexton()).stream()
                        .map(i -> tce.getGraph().getTextonHeader(i).getName()).collect(Collectors.toList()));
        broadcastAll();
        resetVotes();
    }

    @Override
    @OnWebSocketMessage
    public void onMessage(Session session, String str) {
        if ("ping".equals(str)) {
            try {
                session.getRemote().sendString("pong");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("Message reçu d'un client websocket : " + str);
        Vote vote = Vote.valueOf(str);
        getClientsVotesMap().put(session, vote);
        sendVoteUpdate();

        //Send confirmation to client
        try {
            session.getRemote().sendString(confirmationString(vote));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String confirmationString(Vote vote) throws JsonProcessingException {
        //Response example: {"vote": "A"}
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("vote", vote.toString());
        String response = new ObjectMapper().writeValueAsString(node);
        //response = StringEscapeUtils.escapeEcmaScript(response);
        return response;
    }

    @Override
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        getClientsVotesMap().remove(session);
        sendVoteUpdate();
    }

    @Override
    @OnWebSocketConnect
    public void onConnect(Session session) {
        getClientsVotesMap().put(session, Vote.NULL);
        broadcast(session);
        sendVoteUpdate();
    }

    @Override
    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        //Do nothing
    }

    @Override
    public void startBroadcasting() {
        broadcasting.set(true);
    }

    @Override
    public void resetVotes() {
        clientsVotesMap.replaceAll((session, vote) -> Vote.NULL);
        clientsVotesMap.keySet().forEach(session -> {
            try {
                session.getRemote().sendString(confirmationString(Vote.NULL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        sendVoteUpdate();
    }

    //Post vote update on EventBus
    private void sendVoteUpdate() {
        Vote[] voteArrayFull = Vote.values();
        Vote[] voteArrayOnlyVotes = new Vote[voteArrayFull.length - 1];
        System.arraycopy(voteArrayFull, 0, voteArrayOnlyVotes, 0, voteArrayFull.length - 1);
        List<Integer> votes = Arrays.stream(voteArrayOnlyVotes)
                .mapToInt(value -> new Long(getClientsVotesMap().values().stream().filter(value::equals)
                        .count()).intValue()).boxed().collect(Collectors.toList());

        eventBus.post(new VoteChangeEvent(getClientsVotesMap().keySet().size(), votes, this));
    }

    //Immutable class used to store broadcast info.
    private static class BroadcastInfo {
        private int numLiens;
        private int textonCourant;
        private List<String> texts;

        @JsonCreator
        BroadcastInfo(@JsonProperty("numLiens") int numLiens, @JsonProperty("textonCourant") int textonCourant, @JsonProperty("texts") List<String> texts) {
            this.numLiens = numLiens;
            this.textonCourant = textonCourant;
            this.texts = texts;
        }

        @JsonProperty("numLiens")
        public int getNumLiens() {
            return numLiens;
        }

        @JsonProperty("textonCourant")
        public int getTextonCourant() {
            return textonCourant;
        }

        @JsonProperty("texts")
        public List<String> getTexts() {
            return texts;
        }
    }

}
