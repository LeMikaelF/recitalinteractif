package server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

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

    Map<VoteCollector, List<Integer>> voteMap;
    private boolean initialized = false;

    public void init(){
        for (VoteCollector voteCollector : voteCollectors) {
            eventBus.register(voteCollector);
            initialized = true;
        }
    }

    public void startBroadcasting(){
        if(!initialized) throw new IllegalStateException("VoteController is not initialized");
        voteCollectors.forEach(VoteCollector::startBroadcasting);
    }

    @Subscribe
    public void onVoteChangeEvent(VoteChangeEvent voteChangeEvent) {
        voteMap.put(voteChangeEvent.getVoteCollector(), voteChangeEvent.getVotes());
    }

}
