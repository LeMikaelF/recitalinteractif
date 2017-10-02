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

    public void setNumEnr(int numEnr) {
        this.numEnr = numEnr;
    }

    public List<Integer> getVotes() {
        return votes;
    }

    public void setVotes(List<Integer> votes) {
        this.votes = votes;
    }

    public VoteCollector getVoteCollector() {
        return voteCollector;
    }

    public void setVoteCollector(VoteCollector voteCollector) {
        this.voteCollector = voteCollector;
    }

    public VoteChangeEvent(int numEnr, List<Integer> votes, VoteCollector voteCollector) {

        this.numEnr = numEnr;
        this.votes = votes;
        this.voteCollector = voteCollector;
    }
}
