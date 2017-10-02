package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import events.ControlEvent;
import events.TextonChangeEvent;
import javafx.beans.property.*;
import server.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-09-29.
 */
public class CommsManagerImpl implements CommsManager {
//TODO register all eventBus subscribers.

    @Inject
    private VisContr visContr;
    @Inject
    private TabBordContr tabBordContr;
    @Inject
    private EventBus eventBus;
    @Inject
    private Server server;

    private List<IntegerProperty> propVote = Stream.generate(SimpleIntegerProperty::new).limit(5).collect(Collectors.toList());
    private IntegerProperty propNumEnr = new SimpleIntegerProperty();

    {
    }

    @Inject
    //TODO Comment obtenir le serveur ici?
    public CommsManagerImpl(Provider<Server> provider) {
        this.server = provider.get();
        System.out.println("CommsManagerImpl constructor");
        System.out.println("server = " + server);
        server.startServer();
    }

    private void over() {
        eventBus.post(ControlEvent.TERMINE);
    }

    @Subscribe
    public void onTextonChangeEvent(TextonChangeEvent Texton) {
        over();
    }

    @Override
    public Map<String, ReadOnlyIntegerProperty> getProperties() {
        //TODO Peut être changer en Enum? (pourrait même être publique). Inconvénient: ça serait difficile d'ajouter d'autres votes (?).
        //Pas si la première valeur, ou un retour de fonction, est le nombre enregistrés et si les constantes représentent les votes.
        //À ce moment-là, je pourrais itérer sur l'énum au complet et récupérer le nombre d'enregistrés séparément.
        String[] propNames = {"A", "B", "C", "D", "Enr"};

        Map<String, ReadOnlyIntegerProperty> newResult;

        List<ReadOnlyIntegerProperty> list = propVote.stream().map(integerProperty -> {
                    ReadOnlyIntegerWrapper roiw = new ReadOnlyIntegerWrapper();
                    roiw.bind(integerProperty);
                    return roiw.getReadOnlyProperty();
                }

        ).collect(Collectors.toList());

        Map<String, ReadOnlyIntegerProperty> result = new HashMap<>();
        for (int i = 0; i < propNames.length; i++) {
            result.put(propNames[i], list.get(i));
        }
        return result;

    }

    public List<ReadOnlyObjectProperty<Integer>> getVoteProperties() {
        //TODO Mettre les propriétés dans un Map<String, ReadOnlyObjectProperty<Integer>> du style «C, 47»

        //Make all properties read-only
        return propVote.stream().map(integerProperty -> {
            ReadOnlyObjectWrapper<Integer> roow = new ReadOnlyObjectWrapper<>();
            roow.bind(integerProperty.asObject());
            return roow.getReadOnlyProperty();
        }).collect(Collectors.toList());
    }

    public void stopServer() {
        //TODO Écrire la méthode stopServer
    }
}
