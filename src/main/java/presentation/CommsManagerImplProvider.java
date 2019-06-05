package presentation;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:03
 */

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import server.Server;
import server.VoteController;

class CommsManagerImplProvider implements Provider<CommsManagerImpl> {
    @Inject
    Provider<Server> serverProvider;
    @Inject
    private
    Provider<VoteController> voteControllerProvider;
    @Inject
    private
    Provider<EventBus> eventBusProvider;

    public CommsManagerImpl get() {
        return new CommsManagerImpl(serverProvider, voteControllerProvider, eventBusProvider);
    }
}
