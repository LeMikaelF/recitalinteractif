package server;

/**
 * Created by Mikaël on 2017-09-30.
 */
public interface VoteCollector {
    void setNumTextonCourant(int numTextonCourant);
    void startBroadcasting();
    void resetVotes();
}
