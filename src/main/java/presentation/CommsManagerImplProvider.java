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

public class CommsManagerImplProvider implements Provider<CommsManagerImpl> {
    @Inject
    Provider<Server> serverProvider;
    @Inject
    Provider<VoteController> voteControllerProvider;
    @Inject
    Provider<EventBus> eventBusProvider;

    public CommsManagerImpl get() {
        return new CommsManagerImpl(serverProvider, voteControllerProvider, eventBusProvider);
    }
}
