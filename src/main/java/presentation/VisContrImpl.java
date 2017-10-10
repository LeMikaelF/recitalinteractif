package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import events.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Inject
    private EventBus eventBus;

    private ResizableCanvasImpl canvas = new ResizableCanvasImpl();
    private List<IntegerProperty> votes = Stream.generate(SimpleIntegerProperty::new).limit(4).collect(Collectors.toList());
    private IntegerProperty numEnr = new SimpleIntegerProperty();
    private List<Stage> stageList;

    @Inject
    public VisContrImpl(Provider<CommsManager> provider) {
        System.out.println("----------------------------VisContr constructeur------------------------");
        commsManager = provider.get();
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
        Util.initializeStageRetriever(lblNumA, stageList);
    }

    @Subscribe
    private void onPresenterImageUpdateEvent(PresenterImageUpdateEvent presenterImageUpdateEvent) {
        System.out.println("Je suis VisContr et je mets à jour mon image.");
        System.out.println("Les dimensions de l'image sont: " +
                presenterImageUpdateEvent.getImage().getWidth()
         + ", " + presenterImageUpdateEvent.getImage().getHeight());
        canvas.setImage(presenterImageUpdateEvent.getImage());
    }

    @Subscribe
    private void onVoteChangeEvent(VoteChangeEvent voteChangeEvent) {
        for (int i = 0; i < voteChangeEvent.getVotes().length; i++) {
            votes.get(i).set(voteChangeEvent.getVotes()[i]);
            numEnr.set(voteChangeEvent.getNumEnr());
        }
    }

    @Subscribe
    private void onScreenDispatchEvent(ScreenDispatchEvent screenDispatchEvent) {
        screenDispatchEvent.ifYouAreVisRunThis().accept(getStage());
    }

    private Stage getStage(){
        return stageList.get(0);
    }
}
