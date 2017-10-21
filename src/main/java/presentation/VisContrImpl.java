package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.PresenterImageUpdateEvent;
import events.ScreenDispatchEvent;
import events.VoteChangeEvent;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO Transférer le WebView sur la visualisation, et mettre seulement un aperçu en basse résolution (et fréquence) sur le tableau de bord.
//TODO Zoomer sur chaque module (à la prezi) pendant la conclusion, et déplacer la caméra pour animer. À la fin, dé-zoomer et afficher le réseau au complet.
//FIXME Cacher la barre d'outils durant la conclusion, ou bien lui faire écrire les noms des textons.
//FIXME Il y a un effet de letterbox assez marqué. Ajuster l'image à la fenêtre en l'agrandissant.
//TODO Ajouter les options du graphique de vis.js dans une autre fenêtre.
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

    public VisContrImpl() {
        stageProperty.addListener((observable, oldValue, newValue) -> {if(newValue != null) System.out.println(newValue);});
    }

    private ResizableCanvasImpl canvas = new ResizableCanvasImpl();
    private List<IntegerProperty> votes = Stream.generate(SimpleIntegerProperty::new).limit(4).collect(Collectors.toList());
    private IntegerProperty numEnr = new SimpleIntegerProperty();
    private IntegerProperty votesEnr = new SimpleIntegerProperty();
    private ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();

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
    private void onPresenterImageUpdateEvent(PresenterImageUpdateEvent presenterImageUpdateEvent) {
        canvas.setImage(presenterImageUpdateEvent.getImage());
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
