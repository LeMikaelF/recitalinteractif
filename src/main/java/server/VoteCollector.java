package server;

import java.util.List;

/**
 * Created by Mikaël on 2017-09-30.
 */
public interface VoteCollector {
    void setBroadcastInfo(int textonCourant, int numLiens, List<String> texts);
    void startBroadcasting();
    void resetVotes();
}
