package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.*;
import guice.ConclusionBuilderVisJsFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import textonclasses.Graph;
import textonclasses.TextonHeader;
import util.CanvasUtil;
import util.CompositeTextonCanvas;
import util.ResizableDraggableNodeManager;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//FIXME Empêcher tous les clics de se rendre jusqu'au WebView.
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
    private EventBus eventBus;
    @Inject
    private CommsManager commsManager;
    @Inject
    private ConclusionBuilderVisJsFactory conclusionFactory;

    private Node conclusion;
    private CompositeTextonCanvas canvas = new CompositeTextonCanvas();
    private List<IntegerProperty> votes = Stream.generate(SimpleIntegerProperty::new).limit(4).collect(Collectors.toList());
    private IntegerProperty numEnr = new SimpleIntegerProperty();
    private IntegerProperty votesEnr = new SimpleIntegerProperty();
    private ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();
    private boolean conclusionRunning;
    private Graph graph = null;

    public VisContrImpl() {
        stageProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) System.out.println(newValue);
        });
    }

    private void toConclusion(Graph graph) {
        //Nécessaire parce que Jackson ne tolère pas une collection "unmodifiable".
        List<TextonHeader> textonPath = new ArrayList<>(commsManager.getTextonPath());
        ConclusionBuilder conclusionBuilder = conclusionFactory.create(textonPath, graph);
        eventBus.register(conclusionBuilder);
        conclusion = conclusionBuilder.getNode();
        CanvasUtil.setNodeAnchorToAnchorPane(conclusion, 0, 0, 0, 0);
        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(conclusion);

        ResizableDraggableNodeManager.makeNodeDraggable(conclusion);
        ResizableDraggableNodeManager.makeNodeResizableCtrl(conclusion);

        tBarPres.setDisable(true);

        Timeline screencast = new Timeline();
        screencast.setCycleCount(Timeline.INDEFINITE);
        screencast.getKeyFrames().add(new KeyFrame(Duration.millis(1000), event -> {
            SnapshotParameters sp = new SnapshotParameters();
            //sp.setViewport(new Rectangle2D(0, 0, conclusion.getBoundsInParent().getWidth(), conclusion.getBoundsInParent().getHeight()));
            //Image snapshot = conclusion.snapshot(sp, null);
            Image snapshot = conclusion.snapshot(null, null);
            eventBus.post(new PresenterImageUpdateEvent(snapshot));
        }));
        screencast.play();
        conclusionRunning = true;
    }

    private void toRecital() {
        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(canvas);
        tBarPres.setDisable(false);
        conclusion = null;
        conclusionRunning = false;
    }

    @FXML
    void initialize() {
        eventBus.register(this);
        anchorPane.getChildren().add(canvas);
        CanvasUtil.setNodeAnchorToAnchorPane(canvas, 0, 27, 0, 0);
        ResizableDraggableNodeManager.makeNodeDraggable(canvas);
        ResizableDraggableNodeManager.makeNodeResizableCtrl(canvas);
        lblNumA.textProperty().bind(votes.get(0).asString());
        lblNumB.textProperty().bind(votes.get(1).asString());
        lblNumC.textProperty().bind(votes.get(2).asString());
        lblNumD.textProperty().bind(votes.get(3).asString());
        lblTotalVotes.textProperty().bind(numEnr.asString());
        lblVotesEnr.textProperty().bind(votesEnr.asString());
        Util.initializeStageRetriever(lblNumA, stageProperty);
    }

    @Subscribe
    private void onControlObjectEvent(ControlObjectEvent event) {
        if (event.getControlEvent().equals(ControlEvent.CONCLUSION) && event.getObject() instanceof Graph) {
            toConclusion((Graph) event.getObject());
        }
    }

    @Subscribe
    private void onControlEvent(ControlEvent event) {
        if (event.equals(ControlEvent.COMMENCER)) {
            toRecital();
        }
    }

    @Subscribe
    private void onTextonChangeEvent(TextonChangeEvent event) {
        if (graph == null) {
            graph = event.getGraph();
            canvas.setGraph(graph);
        }
        canvas.setTexton(event.getTexton());
    }

    @Subscribe
    public void onVoteChangeEvent(VoteChangeEvent vce) {
        //Has to run on JavaFX thread.
        Platform.runLater(() -> {
            int numTotal = 0;
            for (int i = 0; i < vce.getVotes().size(); i++) {
                votes.get(i).set(vce.getVotes().get(i));
                numTotal += vce.getVotes().get(i);
            }
            votesEnr.set(numTotal);
            numEnr.set(vce.getNumEnr());
        });
    }

    @Subscribe
    private void onScreenDispatchEvent(ScreenDispatchEvent screenDispatchEvent) {
        screenDispatchEvent.ifYouAreVisRunThis().accept(getStage());
    }

    private Stage getStage() {
        return stageProperty.get();
    }
}