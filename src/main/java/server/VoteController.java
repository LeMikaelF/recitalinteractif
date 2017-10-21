package server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.TextonChangeEvent;
import events.VoteChangeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mikaël on 2017-09-30.
 */
public class VoteController {
    @Inject
    EventBus eventBus;

    @Inject
    Set<VoteCollector> voteCollectors;

    Map<VoteCollector, List<Integer>> voteMap = new HashMap<>();
    private boolean initialized = false;
    private boolean wasAskedToBroadcast = false;

    public void init() {
        eventBus.register(this);
        for (VoteCollector voteCollector : voteCollectors) {
            eventBus.register(voteCollector);
            initialized = true;
        }
    }

    public void startBroadcasting() {
        if (!initialized) throw new IllegalStateException("VoteController is not initialized");
        voteCollectors.forEach(VoteCollector::startBroadcasting);
    }

    @Subscribe
    public void onVoteChangeEvent(VoteChangeEvent voteChangeEvent) {
        voteMap.put(voteChangeEvent.getVoteCollector(), voteChangeEvent.getVotes());
    }

    @Subscribe
    public void onTextonChangeEvent(TextonChangeEvent textonChangeEvent) {
        if (!wasAskedToBroadcast) {
            wasAskedToBroadcast = true;
            startBroadcasting();
        } else return;
    }

}
