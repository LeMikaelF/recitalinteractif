package presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import events.ControlEvent;
import events.ControlObjectEvent;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.text.StringEscapeUtils;
import textonclasses.Graph;
import textonclasses.TextonHeader;
import util.FXCustomDialogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikaël on 2017-10-21.
 */

public class ConclusionBuilderVisJs extends ConclusionBuilder {

    private WebEngine webEngine;
    private WebView webView;

    @Inject
    public ConclusionBuilderVisJs(@Assisted List<TextonHeader> path, @Assisted Graph graph) {
        super(path, graph);
    }

    @Override
    public Node getNode() {
        webView = new WebView();

        //This is required to properly serialze a List<TextonHeader>
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        JavaType javaType = typeFactory.constructParametricType(ArrayList.class, TextonHeader.class);

        webEngine = webView.getEngine();
        webEngine.load("http://localhost/conclusion/conclusion.html");
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(State.SUCCEEDED)) {
                try {
                    webEngine.executeScript("receiveJson('" + StringEscapeUtils.escapeEcmaScript(
                            new ObjectMapper().writeValueAsString(getGraph())) + "')");
                    webEngine.executeScript("receiveTextonPath('"
                            + StringEscapeUtils.escapeEcmaScript(
                            new ObjectMapper().writer().forType(javaType).writeValueAsString(getPath())) + "')");
                } catch (JsonProcessingException e) {
                    FXCustomDialogs.showError("Impossible d'initialiser la conclusion. Veuillez réactualiser la page.");
                    e.printStackTrace();
                }
            }
        });

        return webView;
    }

    @Subscribe
    private void onControlEvent(ControlEvent event) {
        switch (event) {
            case SUIVANT:
                //Move forward.
                webEngine.executeScript("moveForwardInGraph()");
                break;
            case PHYSON:
                //Turn physics on.
                webEngine.executeScript("setPhysics(" + true + ")");
                break;
            case PHYSOFF:
                //Turn physics off.
                webEngine.executeScript("setPhysics(" + false + ")");
            default:
                break;
        }
    }

    @Subscribe
    private void onControlObjectEvent(ControlObjectEvent event) {
        if (event.getControlEvent().equals(ControlEvent.KEYTYPED) && event.getObject() instanceof Event) {
            Platform.runLater(() -> webView.fireEvent((Event) event.getObject()));
        }
    }
}
