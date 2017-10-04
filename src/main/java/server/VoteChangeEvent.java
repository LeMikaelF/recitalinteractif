package server;

import java.util.List;

/**
 * Created by Mikaël on 2017-09-30.
 */
public class VoteChangeEvent {
    private int numEnr;
    private List<Integer> votes;
    private VoteCollector voteCollector;

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
