package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import events.ControlEvent;
import events.TextonChangeEvent;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import textonclasses.Texton;
import util.CompositeTextonCanvas;
import util.Graph;
import util.ResizableDraggableNodeManager;

import java.util.HashMap;
import java.util.Map;

public class VisContrImpl implements VisContr {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ToolBar tBarPres;
    @FXML
    private Label lblNumA;
    @FXML
    private Label lblNumB;
    @FXML
    private Label lblNumC;
    @FXML
    private Label lblNumD;
    @FXML
    private Label lblVotesEnr;
    @FXML
    private Label lblTotalVotes;

    @Inject
    private CommsManager commsManager;

    private Map<String, ReadOnlyIntegerProperty> properties = new HashMap<>();
    private CompositeTextonCanvas textonCanvas = new CompositeTextonCanvas();
    @Inject
    private EventBus eventBus;


    @Inject
    public VisContrImpl(Provider<CommsManager> provider) {
        ResizableDraggableNodeManager.makeNodeDraggable(textonCanvas);
        ResizableDraggableNodeManager.makeNodeResizableCtrl(textonCanvas);
        commsManager = provider.get();
        properties = commsManager.getProperties();
        System.out.println("----------------------------VisContr constructeur------------------------");
    }

    private void changeTexton(Texton texton, Graph graph) {
        textonCanvas.setTexton(texton);
        textonCanvas.setGraph(graph);
    }

    @FXML
    void initialize() {
        lblNumA.textProperty().bind(properties.get("A").asString());
        lblNumB.textProperty().bind(properties.get("B").asString());
        lblNumC.textProperty().bind(properties.get("C").asString());
        lblNumD.textProperty().bind(properties.get("D").asString());
        lblTotalVotes.textProperty().bind(properties.get("Enr").asString());
        lblVotesEnr.textProperty().bind(Bindings.add(properties.get("A"),
                Bindings.add(properties.get("B"),
                        Bindings.add(properties.get("C"),
                                properties.get("D")))).asString());

    }

    @Subscribe
    private void onTextonChangeEvent(TextonChangeEvent tce) {
        changeTexton(tce.getTexton(), tce.getGraph());
    }

    @Subscribe
    private void onEventCode(ControlEvent code) {
        switch (code) {
            case INSTALLER:
                installer();
                break;
            case RESTAURER:
                restaurer();
                break;
            case TERMINE:
                over();
                break;
        }
    }

    private void over() {
        //TODO Passer à la conclusion.
    }

    private void restaurer() {
        //TODO Restaurer la présenttion. Le code est dans Contrpres.
    }

    private void installer() {
        //TODO Installer la présentation. Le code est dans ContrPres.
    }
}
