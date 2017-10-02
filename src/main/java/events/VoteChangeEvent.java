package events;

import server.VoteController;

/**
 * Created by Mikaël on 2017-10-01.
 */
public class VoteChangeEvent {

    final private int[] votes;
    final private int numEnr;
    final private VoteController source;

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
