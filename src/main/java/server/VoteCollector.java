package server;

/**
 * Created by Mikaël on 2017-09-30.
 */
public interface VoteCollector {
    void setBroadcastInfo(int textonCourant, int numLiens);
    void startBroadcasting();
    void resetVotes();
}
