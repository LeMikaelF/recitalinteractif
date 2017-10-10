package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import events.ControlEvent;
import events.TextonChangeEvent;
import javafx.beans.property.*;
import server.Server;
import server.VoteController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-09-29.
 */
public class CommsManagerImpl implements CommsManager {

    @Inject
    private VisContr visContr;
    @Inject
    private TabBordContr tabBordContr;
    private EventBus eventBus;
    private Server server;
    private VoteController voteController;

    private List<IntegerProperty> propVote = Stream.generate(SimpleIntegerProperty::new).limit(5).collect(Collectors.toList());
    private IntegerProperty propNumEnr = new SimpleIntegerProperty();


    @Inject
    public CommsManagerImpl(Provider<Server> serverProvider, Provider<VoteController> voteControllerProvider, Provider<EventBus> eventBusProvider) {
        server = serverProvider.get();
        voteController = voteControllerProvider.get();
        eventBus = eventBusProvider.get();
        eventBus.register(this);
        System.out.println("VoteController = " + voteController);
        voteController.init();
        server.startServer();
    }

    @Subscribe
    public void onControlEvent(ControlEvent controlEvent) {
        if(controlEvent.equals(ControlEvent.SHUTDOWN)) try {
            server.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
