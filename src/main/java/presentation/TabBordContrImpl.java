package presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.*;
import io.TextonIoFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.EnumUtils;
import server.Vote;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonHeader;
import util.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabBordContrImpl implements TabBordContr {

    @FXML
    private Menu menuFichier;
    @FXML
    private MenuItem menuOuvrirRecital;
    @FXML
    private MenuItem menuNaviguerAuTexton;
    @FXML
    private MenuItem menuLienA;
    @FXML
    private MenuItem menuLienB;
    @FXML
    private MenuItem menuLienC;
    @FXML
    private MenuItem menuLienD;
    @FXML
    private MenuItem menuPrecedent;
    @FXML
    private MenuItem menuInstaller;
    @FXML
    private MenuItem menuRestaurer;
    @FXML
    private MenuItem menuFermer;
    @FXML
    private MenuItem menuConclusion;
    @FXML
    private AnchorPane anchorPaneTabBord;
    @FXML
    private TextArea textAreaTexte;
    @FXML
    private Label lblNumEnr;
    @FXML
    private Label lblNumA;
    @FXML
    private Label lblNumB;
    @FXML
    private Label lblNumC;
    @FXML
    private Label lblNumD;
    @FXML
    private VBox vBoxButtons;
    @FXML
    private Button btnTermine;
    @FXML
    private CheckBox checkBoxPhysics;
    @FXML
    private Label lblNomTexton;
    @FXML
    private Label lblNumTexton;
    @FXML
    private TextArea textAreaSource;
    @FXML
    private Label lblHorlTexton;
    @FXML
    private Label lblHorlRecital;
    @FXML
    private Label lblHorloge;
    private final ImageView imageView = new ImageView();
    private final Path path = Paths.get(PropLoader.getMap().get("location"));
    private final CompositeTextonCanvas tcTabBord = new CompositeTextonCanvas();

    private final ChangeListener<Boolean> conclusionNodeOperations = (observable, oldValue, newValue) -> {
        if (newValue) {
            CanvasUtil.setNodeAnchorToAnchorPane(imageView, 0, 0, 0, 0);
            anchorPaneTabBord.getChildren().clear();
            anchorPaneTabBord.getChildren().add(imageView);
        } else {
            CanvasUtil.setNodeAnchorToAnchorPane(tcTabBord, 0, 0, 0, 0);
            anchorPaneTabBord.getChildren().clear();
            anchorPaneTabBord.getChildren().add(tcTabBord);
        }
    };

    private long recitalClock;
    private long textonClock;
    private Texton texton;
    private Texton textonPrecedent;
    private final ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();
    private Graph graph;
    private final List<IntegerProperty> votes = Stream.generate(SimpleIntegerProperty::new).limit(4).collect(Collectors.toList());
    private final IntegerProperty numEnr = new SimpleIntegerProperty();
    @Inject
    private EventBus eventBus;
    @Inject
    private TextonIoFactory textonIoFactory;
    @Inject
    private ScreenDispatcher screenDispatcher;
    //This needs to be here because the constructor runs some necessary actions.
    @Inject
    private CommsManager commsManager;
    private final SimpleBooleanProperty conclusionRunning = new SimpleBooleanProperty(false);

    @Inject
    public TabBordContrImpl() {
    }

    private Stage getStage() {
        return stageProperty.get();
    }

    @FXML
    private void menuHandler(ActionEvent event) {
        try {
            if (event.getSource().equals(menuFermer)) {
                getStage().close();
            } else if (event.getSource().equals(menuLienA)) {
                changeTexton(Vote.A);
            } else if (event.getSource().equals(menuLienB)) {
                changeTexton(Vote.B);
            } else if (event.getSource().equals(menuLienC)) {
                changeTexton(Vote.C);
            } else if (event.getSource().equals(menuLienD)) {
                changeTexton(Vote.D);
            } else if (event.getSource().equals(menuNaviguerAuTexton)) {
                naviguerAuTexton();
            } else if (event.getSource().equals(menuOuvrirRecital)) {
                changeTexton(1);
                startRecitalTimer();
            } else if (event.getSource().equals(menuPrecedent)) {
                precedent();
            } else if (event.getSource().equals(menuRestaurer)) {
                restaurerPres();
            } else if (event.getSource().equals(menuInstaller)) {
                installerPres();
            } else if (event.getSource().equals(menuConclusion)) {
                toConclusion();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnTermine)) {
            changeTexton();
        }
    }

    private void changeTexton() {
        //If no votes have been registered
        if (votes.stream().allMatch(integerProperty -> integerProperty.get() == 0)) {
            String input = FXCustomDialogs.showInput("Aucun vote n'a été enregistré. Choisissez le lien que vous voulez suivre (A, B, C, D) :");
            if (EnumUtils.isValidEnum(Vote.class, input)) {
                Vote vote = Vote.valueOf(input);
                try {
                    changeTexton(vote);
                } catch (IOException e) {
                    FXCustomDialogs.showInput("Veuillez entrer un vote valide (p. ex. : A, B, C…)");
                    changeTexton();
                    e.printStackTrace();
                }
            }
        } else {
            //TODO Verify max-checking code
            //Determine which link has the most votes
            int max = 0;
            Vote vote = Vote.A;
            for (int i = 0; i < votes.size() - 1; i++) {
                if (max < votes.get(i).get()) {
                    max = votes.get(i).get();
                    vote = Vote.values()[i];
                }
            }
            try {
                changeTexton(vote);
            } catch (IOException e) {
                FXCustomDialogs.showException(e);
                e.printStackTrace();
            }
        }
    }

    private void changeTexton(Vote vote) throws IOException {
        if (graph.getChildren(texton.getNumTexton()).size() <= Arrays.asList(Vote.values()).indexOf(vote)) {
            //Le lien choisi n'existe pas parce que le texton n'a pas assez de liens.
            FXCustomDialogs.showError("Vote invalide. Le texton ne possède pas assez de liens sortants.");
        }
        int numTexton = graph.getChildren(texton.getNumTexton()).get(Arrays.asList(Vote.values()).indexOf(vote));
        changeTexton(numTexton);
    }

    private void changeTexton(int numTexton) throws IOException {
        Texton texton = textonIoFactory.create(path).readTexton(numTexton);
        changeTexton(texton);
    }

    private void changeTexton(Texton texton) {
        //All changeTexton methods delegate to this one.
        if (texton == null)
            throw new IllegalArgumentException("Le texton fourni à la méthode changeTexton(Texton texton) est null.");
        textonPrecedent = texton;
        this.texton = texton;
        eventBus.post(new TextonChangeEvent(texton, graph));

        //Set all fields
        tcTabBord.setTexton(texton);
        lblNumTexton.textProperty().set(String.valueOf(texton.getNumTexton()));
        lblNomTexton.textProperty().set(texton.getName());
        textAreaSource.textProperty().set(texton.getSource());
        textAreaTexte.textProperty().set(texton.getDescription());
    }

    private void installerPres() {
        screenDispatcher.sendMaximizeEvent(getStage());
    }

    @Subscribe
    private void onScreenDispatchEvent(ScreenDispatchEvent screenDispatchEvent) {
        screenDispatchEvent.ifYouAreTabBordRunThis().accept(getStage());
    }

    private void restaurerPres() {
        screenDispatcher.sendRestoreEvent(getStage());
    }

    private void naviguerAuTexton() throws IOException {
        String choix = FXCustomDialogs.showInput("Naviguer vers le texton…");
        if (choix != null) {
            int numChoisi = Integer.parseInt(choix);
            changeTexton(numChoisi);
        }
    }

    private void precedent() {
        if (textonPrecedent != null) {
            changeTexton(textonPrecedent);
        }
    }

    private void toConclusion() {
        eventBus.post(new ControlObjectEvent(graph, ControlEvent.CONCLUSION));
        List<TextonHeader> textonPath = commsManager.getTextonPath();
        Iterator<TextonHeader> iterator = textonPath.iterator();

        //Change behaviour of "terminé" button to control toConclusion.
        btnTermine.textProperty().set("Texton suivant");
        btnTermine.onActionProperty().set(event -> {
            //Qui est-ce qui lit les SUIVANT?
            eventBus.post(ControlEvent.SUIVANT);
            if (iterator.hasNext()) {
                lblNomTexton.setText(iterator.next().getName());
            } else {
                lblNomTexton.setText("Parcours terminé");
            }
        });
        //Make physics checkbox visible and set correct behaviour.
        if (!checkBoxPhysics.isVisible()) {
            checkBoxPhysics.setVisible(true);
            checkBoxPhysics.selectedProperty().set(true);
            checkBoxPhysics.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) eventBus.post(ControlEvent.PHYSON);
                else eventBus.post(ControlEvent.PHYSOFF);
            });
        }

        //TODO Pour l'instant, la toConclusion affiche simplement le parcours hypertextuel au format json brut.
        //Amélioration possible: passer le List<TextonHeader> dans un TableView<TextonHeader>
        try {
            textAreaTexte.textProperty().set(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(textonPath));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //Display network hotkeys
        textAreaSource.textProperty().set("Pg Haut/Bas: zoom \n flèches: déplacer le graphe\n Faites entrer la souris dans la fenêtre de prévisualisation" +
                "pour actualiser la fenêtre de présentation publique.");

        //Post key types on eventBus
        getStage().addEventFilter(KeyEvent.ANY, event -> {
            event.consume();
            eventBus.post(new ControlObjectEvent(event, ControlEvent.KEYTYPED));
        });

        conclusionRunning.set(true);
    }

    private void toRecital() {
        conclusionRunning.set(false);
        //Revenir au mode récital.
    }

    @FXML
    void initialize() throws IOException {

        eventBus.register(this);
        Util.initializeStageRetriever(textAreaSource, stageProperty);
        graph = textonIoFactory.create(path).getGraph();
        tcTabBord.setGraph(graph);

        Stream.of(textAreaSource, textAreaTexte).forEach(textArea -> textArea.setWrapText(true));

        lblNumA.textProperty().bind(votes.get(0).asString());
        lblNumB.textProperty().bind(votes.get(1).asString());
        lblNumC.textProperty().bind(votes.get(2).asString());
        lblNumD.textProperty().bind(votes.get(3).asString());
        lblNumEnr.textProperty().bind(numEnr.asString());

        CanvasUtil.setNodeAnchorToAnchorPane(tcTabBord, 0, 0, 0, 0);
        anchorPaneTabBord.getChildren().add(tcTabBord);

        setupClocks();

        //Conclusion stuff
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(anchorPaneTabBord.widthProperty());
        imageView.fitHeightProperty().bind(anchorPaneTabBord.heightProperty());
        conclusionRunning.addListener(conclusionNodeOperations);
    }

    @Subscribe
    private void onControlEvent(ControlEvent event) {
        if (event.equals(ControlEvent.CONCLUSION)) {
            conclusionRunning.set(true);
            anchorPaneTabBord.getChildren().clear();
            anchorPaneTabBord.getChildren().add(imageView);
        }
        if (event.equals(ControlEvent.COMMENCER)) {
            toRecital();
        }
    }

    @Subscribe
    private void onPresenterImageUpdateEvent(PresenterImageUpdateEvent event) {
        if (conclusionRunning.get()) {
            //This line cuts down memory usage by half.
            imageView.setImage(null);
            imageView.setImage(event.getImage());
        }
    }

    @Subscribe
    public void onVoteChangeEvent(VoteChangeEvent vce) {
        //Has to run on JavaFX thread.
        Platform.runLater(() -> {
            for (int i = 0; i < vce.getVotes().size(); i++) {
                votes.get(i).set(vce.getVotes().get(i));
            }
            numEnr.set(vce.getNumEnr());
        });
    }

    private void setupClocks() {
        resetTextonClock();
        EventHandler<ActionEvent> clockEvent = event -> {
            updateClock();
            updateTextonClock();
            if (recitalClock != 0)
                updateRecitalClock();
        };

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(new Duration(1000), clockEvent));
        timeline.play();
    }

    private void startRecitalTimer() {
        recitalClock = System.currentTimeMillis();
    }

    private void updateRecitalClock() {
        long currentClock = System.currentTimeMillis() - recitalClock;
        lblHorlRecital.textProperty().set(getFormattedTime(currentClock));
    }

    private void updateClock() {
        lblHorloge.textProperty().set(new SimpleDateFormat("kk:mm:ss").format(new Date()));
    }

    private void updateTextonClock() {
        long currentClock = System.currentTimeMillis() - textonClock;
        lblHorlTexton.textProperty().set(getFormattedTime(currentClock));
    }

    private void resetTextonClock() {
        textonClock = System.currentTimeMillis();
    }

    private String getFormattedTime(long time) {
        /*La prochaine instruction est adaptée de : siddhadev (nom d'utilisateur) et Dave Jarvis. 2016. Réponse à « How
        to convert Milliseconds to “X mins, x seconds” in Java? ». Stack Overflow. 1er septembre. Consulté le 20
        décembre 2016.
        <http://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java>*/
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    private Timeline blurDuringTransition(Pane pane, Runnable middleAction) {
        Timeline blurAnim = new Timeline();
        GaussianBlur effect = new GaussianBlur(0);
        pane.setEffect(effect);
        blurAnim.setCycleCount(2);
        blurAnim.getKeyFrames().add(new KeyFrame(Duration.seconds(2), event -> {
            blurAnim.pause();
            middleAction.run();
        }, new KeyValue(effect.radiusProperty(), 63)));
        blurAnim.setAutoReverse(true);
        return blurAnim;
    }

}
