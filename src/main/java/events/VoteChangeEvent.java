package events;

import server.VoteCollector;

import java.util.List;

/**
 * Created by Mikaël on 2017-09-30.
 */
public class VoteChangeEvent {
    private final int numEnr;
    private final List<Integer> votes;
    private final VoteCollector voteCollector;

    public int getNumEnr() {
        return numEnr;
    }

    public List<Integer> getVotes() {
        return votes;
    }

    public VoteCollector getVoteCollector() {
        return voteCollector;
    }

    public VoteChangeEvent(int numEnr, List<Integer> votes, VoteCollector voteCollector) {
        this.numEnr = numEnr;
        this.votes = votes;
        this.voteCollector = voteCollector;
    }
}
