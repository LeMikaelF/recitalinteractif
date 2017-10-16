package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import events.ControlEvent;
import events.TextonChangeEvent;
import server.Server;
import server.VoteController;
import textonclasses.TextonHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mikaël on 2017-09-29.
 */
public class CommsManagerImpl implements CommsManager {

    private EventBus eventBus;
    private Server server;
    private VoteController voteController;
    private TextonChangeLogger textonChangeLogger = new TextonChangeLogger();

    @Inject
    public CommsManagerImpl(Provider<Server> serverProvider, Provider<VoteController> voteControllerProvider, Provider<EventBus> eventBusProvider) {
        server = serverProvider.get();
        voteController = voteControllerProvider.get();
        eventBus = eventBusProvider.get();
        eventBus.register(this);
        eventBus.register(textonChangeLogger);
        voteController.init();
        server.startServer();
    }

    @Subscribe
    public void onControlEvent(ControlEvent controlEvent) {
        if (controlEvent.equals(ControlEvent.SHUTDOWN)) try {
            server.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TextonHeader> getTextonPath() {
        return textonChangeLogger.getPath();
    }

    public class TextonChangeLogger {
        private List<TextonHeader> path = new ArrayList<>();

        public List<TextonHeader> getPath() {
            return Collections.unmodifiableList(path);
        }

        @Subscribe
        private void onTextonChangeEvent(TextonChangeEvent event) {
            path.add(event.getTexton().getTextonHeader());
        }
    }

}
