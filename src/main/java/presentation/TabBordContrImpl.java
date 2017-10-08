package presentation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import events.PresenterImageUpdateEvent;
import events.TextonChangeEvent;
import io.TextonIo;
import io.TextonIoFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.apache.commons.lang3.EnumUtils;
import server.Vote;
import server.VoteChangeEvent;
import textonclasses.Graph;
import textonclasses.Texton;
import util.CanvasUtil;
import util.CompositeTextonCanvas;
import util.FXCustomDialogs;
import util.PropLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
    private Button btnTermine;
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

    private Path path = Paths.get(PropLoader.getMap().get("location"));

    private CompositeTextonCanvas tcTabBord = new CompositeTextonCanvas();
    private long recitalClock;
    private long textonClock;
    private Texton texton = null;
    private Texton textonPrecedent = null;

    //TODO Vérifieer l'initialisation du Graph.
    private Graph graph;
    private List<IntegerProperty> votes = Stream.generate(SimpleIntegerProperty::new).limit(4).collect(Collectors.toList());
    private IntegerProperty numEnr = new SimpleIntegerProperty();
    @Inject
    private EventBus eventBus;
    @Inject
    private CommsManager commsManager;
    @Inject
    private TextonIoFactory textonIoFactory;

    //TODO Ajouter la citation, provient de https://stackoverflow.com/questions/28581639/javafx8-presentation-view-duplicate-pane-and-content
    private ChangeListener<Boolean> needsLayoutListener = (observable, oldValue, newValue) -> {
        if (!newValue)
            eventBus.post(new PresenterImageUpdateEvent(anchorPaneTabBord.snapshot(new SnapshotParameters(), null)));
    };

    @Inject
    public TabBordContrImpl() throws IOException, URISyntaxException {
        System.out.println("-------------------------------tabbordcontr constructeur--------------");
    }

    @FXML
    private void menuHandler(ActionEvent event) {
        try {
            if (event.getSource().equals(menuFermer)) {

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
                conclusion();
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
                    e.printStackTrace();
                    changeTexton();
                }
            }
        }

        //Determine which link has the most votes
        int max = 0;
        Vote vote = Vote.A;
        for (int i = 0; i < votes.size() - 1; i++) {
            if (max < votes.get(i).get()) vote = Vote.values()[i];
        }
        try {
            changeTexton(vote);
        } catch (IOException e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
    }

    private void changeTexton(Vote vote) throws IOException {
        int numTexton = graph.getChildren(texton.getNumTexton()).get(Arrays.asList(Vote.values()).indexOf(vote));
        changeTexton(numTexton);
    }

    private void changeTexton(int numTexton) throws IOException {
        Texton texton = textonIoFactory.create(path).readTexton(numTexton);
        changeTexton(texton);
    }

    private void changeTexton(Texton texton) {
        eventBus.post(new TextonChangeEvent(texton, graph));

        //Set all fields
        tcTabBord.setTexton(texton);
        lblNumTexton.textProperty().set(texton.getName());
        textAreaSource.textProperty().set(texton.getSource());
        textAreaTexte.textProperty().set(texton.getDescription());
    }

    private void installerPres() {
        eventBus.post("installer");
    }

    private void restaurerPres() {
        eventBus.post("restaurer");
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

    private void conclusion() {
        anchorPaneTabBord.getChildren().clear();
        WebView webView = new WebView();
        CanvasUtil.setNodeAnchorToAnchorPane(webView, 0, 0, 0, 0);
        anchorPaneTabBord.getChildren().add(webView);
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/webview/conclusion/conclusion.html").toExternalForm());

        //Add listeners to copy image to VisContr
        Runnable copyImage = () -> eventBus.post(new PresenterImageUpdateEvent(webView.snapshot(new SnapshotParameters(), null)));

        Timeline screencast = new Timeline();
        screencast.setCycleCount(Timeline.INDEFINITE);
        screencast.getKeyFrames().add(new KeyFrame(Duration.millis(30), "screenshot", event -> copyImage.run()));

        Timeline stopScreenCast = new Timeline();
        stopScreenCast.setCycleCount(1);
        stopScreenCast.getKeyFrames().add(new KeyFrame(Duration.seconds(3), event -> screencast.stop()));

        Runnable updateForAWhile = () -> {
            screencast.playFromStart();
            stopScreenCast.playFromStart();
        };

        webView.setOnMouseEntered(event -> {
            updateForAWhile.run();
        });
        webView.setOnMouseReleased(event -> stopScreenCast.playFromStart());

        //Fit graph on double-click
        webView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                webEngine.executeScript("fitGraph()");
            }
        });

        //This runs right right away
        updateForAWhile.run();
    }

    @FXML
    void initialize() {

        eventBus.register(this);
        Stream.of(textAreaSource, textAreaTexte).forEach(textArea -> textArea.setWrapText(true));

        lblNumA.textProperty().bind(votes.get(0).asString());
        lblNumB.textProperty().bind(votes.get(1).asString());
        lblNumC.textProperty().bind(votes.get(2).asString());
        lblNumD.textProperty().bind(votes.get(3).asString());
        lblNumEnr.textProperty().bind(numEnr.asString());

        CanvasUtil.setNodeAnchorToAnchorPane(tcTabBord, 0, 0, 0, 0);
        anchorPaneTabBord.getChildren().add(tcTabBord);

        //Update EventBus with changes to anchorPanePres
        anchorPaneTabBord.needsLayoutProperty().addListener(needsLayoutListener);

        setupClocks();
    }

    @Subscribe
    public void onVoteChangeEvent(VoteChangeEvent vce) {
        for (int i = 0; i < vce.getVotes().size(); i++) {
            votes.get(i).set(vce.getVotes().get(i));
        }
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
