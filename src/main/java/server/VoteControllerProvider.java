package server;

import com.google.inject.Provider;

/**
 * Created by Mikaël on 2017-10-03.
 */
class VoteControllerProvider implements Provider<VoteController> {

    @Override
    public VoteController get() {
        return new VoteController();
    }
}
