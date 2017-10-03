package presentation;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import events.TextonChangeEvent;
import io.TextonIo;
import io.XmlFileConnector;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import server.Vote;
import textonclasses.Texton;
import util.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class TabBordContrImpl implements TabBordContr {

    private CompositeTextonCanvas tcTabBord = new CompositeTextonCanvas();
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
    //TODO Ajouter menuConclusion au handler.
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
    private long recitalClock;
    private long textonClock;
    private Texton textonPrecedent = null;
    private TextonIo textonIo = new XmlFileConnector(new String[]{PropLoader.getMap().get("location")});
    private Graph graph = new Graph(textonIo.getGraphXml());
    @Inject
    private EventBus eventBus;

    @Inject
    private CommsManager commsManager;

    @Inject
    public TabBordContrImpl() throws IOException, URISyntaxException {
        System.out.println("-------------------------------tabbordcontr constructeur--------------");
    }

    @FXML
    private void menuHandler(ActionEvent event) {
        //TODO Compléter le menu handler
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
        }

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnTermine)) {
            changeTexton();
        }
    }

    private void changeTexton() {
        //TODO Adapter de ContrPres
    }

    private void changeTexton(int numTexton) {
        //TODO Adapter de ContrPres
    }

    private void changeTexton(Vote vote) {
        //TODO Adapter de ContrPres
    }

    private void changeTexton(Texton texton) {
        eventBus.post(new TextonChangeEvent(texton, graph));
        //TODO Écrire la méthode changeTexton(texton);
    }

    private void installerPres() {
        eventBus.post("installer");
    }

    private void restaurerPres() {
        eventBus.post("restaurer");
    }

    private void naviguerAuTexton() {
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

    @FXML
    void initialize() {
        Map<String, ReadOnlyIntegerProperty> properties = commsManager.getProperties();

        System.out.println(properties);
        System.out.println(lblNumA);
        lblNumA.textProperty().bind(properties.get("A").asString());
        lblNumB.textProperty().bind(properties.get("B").asString());
        lblNumC.textProperty().bind(properties.get("C").asString());
        lblNumD.textProperty().bind(properties.get("D").asString());
        lblNumEnr.textProperty().bind(properties.get("Enr").asString());
        Stream.of(textAreaSource, textAreaTexte).forEach(textArea -> textArea.setWrapText(true));

        CanvasUtil.setNodeAnchorToAnchorPane(tcTabBord, 0, 0, 0, 0);
        anchorPaneTabBord.getChildren().add(tcTabBord);

        setupClocks();
        System.out.println("------------tabbourdinitialization complete-------------");
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
