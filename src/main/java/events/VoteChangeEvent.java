package events;

import server.VoteController;

/**
 * Created by Mikaël on 2017-10-01.
 */
public class VoteChangeEvent {

    private final int[] votes;
    private final int numEnr;
    private final VoteController source;

    public int[] getVotes() {
        return votes;
    }

    public int getNumEnr() {
        return numEnr;
    }

    public VoteController getSource() {
        return source;
    }

    public VoteChangeEvent(int[] votes, int numEnr, VoteController source) {

        this.votes = votes;
        this.numEnr = numEnr;
        this.source = source;
    }
}
