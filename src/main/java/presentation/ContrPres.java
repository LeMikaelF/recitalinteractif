package presentation;

/**
 * Created by Mikaël on 2016-11-03.
 **/

import anim.ChangeAnim;
import anim.ChangeAnimFactory;
import anim.CounterAnim;
import anim.CounterFactory;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.*;
import textonIO.SqlConnector;
import textonclasses.Texton;
import textonclasses.TextonV;
import util.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ContrPres {

    final private int COUNTERHIDEDELAY = 5000;
    private final Timeline blurAnim = new Timeline();
    private final BooleanProperty transitionPeakFlag = new SimpleBooleanProperty(false);
    private final BooleanProperty timerRunning = new SimpleBooleanProperty(false);
    private final PollController pollController;
    private double fadeIndex = 1;

    //Nodes from TableauBord.fxml
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
    private TextArea textAreaTexte;
    @FXML
    private Label lblNumEnregistres;
    @FXML
    private Label lblNumA;
    @FXML
    private Label lblNumB;
    @FXML
    private Label lblNumC;
    @FXML
    private Label lblNumD;
    @FXML
    private Label lblNumSeuil;
    @FXML
    private ToggleButton tglSeuil;
    @FXML
    private Label lblNumTexton;
    @FXML
    private Label lblType;
    @FXML
    private Label lblTimer;
    @FXML
    private TextArea textAreaSource;
    @FXML
    private Label lblHorlTexton;
    @FXML
    private Label lblHorlRecital;
    @FXML
    private Label lblHorloge;
    @FXML
    private AnchorPane anchorPaneTabBord;

    //Nodes from Visualisation.fxml
    @FXML
    private AnchorPane anchorPanePres;
    @FXML
    private ToolBar tBarPres;
    @FXML
    private Label lblVotesEnregistresVis;
    @FXML
    private Label lblSeuilPresText;
    @FXML
    private Label lblSeuilPres;
    @FXML
    private Button btnTermine;
    @FXML
    private Label lblNumAVis;
    @FXML
    private Label lblNumBVis;
    @FXML
    private Label lblNumCVis;
    @FXML
    private Label lblNumDVis;
    @FXML
    private Label lblTotalVotesVis;

    private CompositeTextonCanvas tcVis;
    private CompositeTextonCanvas tcTabBord;
    private Texton lastTexton = null;
    private Texton curTexton = null;
    private Stage stageTableau;
    private Stage stageVis;
    private final ChangeListener<Boolean> changeListenerSelectedProperty = (observable, oldValue, newValue) -> {
        if (!newValue) {
            //No action is to be performed when critical proportion is deactivated.
            lblSeuilPres.visibleProperty().set(false);
        } else {
            //Animation du seuil critique sur la fenêtre de présentation.
            lblSeuilPres.visibleProperty().set(true);
            Text text = getCriticalPropVisText();
            anchorPanePres.getChildren().add(text);

            Timeline tl = new Timeline();
            tl.setCycleCount(20);
            tl.getKeyFrames().add(new KeyFrame(new Duration(200), event -> {
                text.setFill(Color.color(1, 0, 0, fadeIndex));
                fadeIndex -= 1 / (double) tl.getCycleCount();
            }));
            tl.setOnFinished(event -> {
                fadeIndex = 1;
                anchorPanePres.getChildren().remove(text);
            });
            tl.play();
        }
    };
    private boolean isInitialised = false;
    private SqlConnector sqlConnector;
    private Task pollContTask;
    private boolean isPresInstalled = false;
    private long textonClock;
    private long recitalClock;
    private IntegerProperty propA;
    private IntegerProperty propB;
    private IntegerProperty propC;
    private IntegerProperty propD;
    private IntegerProperty propNumEnr;
    private final ChangeListener<String> pollThreadListener = (observable, oldValue, newValue) -> {
        //Actions to do on vote counter change and critical proportion change.
        System.out.println("Message reçu du Thread : " + pollContTask.getMessage());
        if (newValue.contains("!")) {
            if (tglSeuil.isSelected()) {
                criticalChangeTexton(ControlCode.valueOf(newValue.substring(1)));
            }
            return;
        }
        int[] votes = new int[5];
        votes = PollController.StringToVotes(newValue);
        propA.set(votes[0]);
        propB.set(votes[1]);
        propC.set(votes[2]);
        propD.set(votes[3]);
        propNumEnr.set(votes[4]);
    };
    private IntegerProperty propTimer;
    private final ChangeListener<Number> countDownListener = (observable, oldValue, newValue) -> {
        propTimer.set(newValue.intValue());
        lblTimer.textProperty().set(getFormattedTime(newValue.longValue()));
    };
    private Node cCounter;
    private final ChangeListener<Boolean> countDownOverListener = (observable, oldValue, newValue) -> {
        //Actions to do at end of TV timer.
        if (!newValue) return;
        doTimerOverAction();
        Timeline hideDelay = new Timeline();
        hideDelay.setCycleCount(1);
        hideDelay.getKeyFrames().add(new KeyFrame(new Duration(COUNTERHIDEDELAY), event1 -> anchorPanePres.getChildren().remove(cCounter)));
        hideDelay.play();
    };

    ContrPres() {
        pollController = new PollController(1, this);
        SocketQueueHandler.setPollController(pollController);
        Thread serverThread = new Thread(() -> {
            try {
                startServer();
            } catch (Exception e) {
                FXCustomDialogs.showError("Error initializing server. See stack trace.");
                e.printStackTrace();
            }
        });
        serverThread.start();
        SocketQueueHandler.startBroadcasting();
    }

    private void startServer() {
        try {
            Server.startServer();
        } catch (Exception e) {
            System.out.println("Error initializing server.");
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
    }

    private void changeTexton(int numTexton) {
        if (propA.add(propB.add(propC.add(propD))).getValue().intValue() == 0 && propNumEnr.get() > 0 && curTexton != null) {
            String msg = "Aucun vote n'a été enregistré. Voulez-vous retourner au texton d'accueil?";
            if (FXCustomDialogs.showConfirmationAction(msg)) {
                numTexton = 1;
            }
        }
        try {
            Texton tempTexton = sqlConnector.readTexton(numTexton);
            if (tempTexton == null) {
                FXCustomDialogs.showError("Error creating texton.");
                return;
            }
            installTexton(tempTexton);
            pollController.changePollCounter();
            pollController.getQueue().add(ControlCode.NEWQUESTION);
            SocketQueueHandler.setBroadcastInfo(curTexton.getNumTexton(), curTexton.getLienTexton().size());
        } catch (Exception e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
    }

    private void changeTexton(ControlCode choice) {
        changeTexton(curTexton.getLienTexton().get(Util.ccToInt(choice)).getNumSerie());
    }

    private void changeTexton() {
        changeTexton(pollController.getPollCounter().getMax());
    }

    private void criticalChangeTexton(ControlCode cc) {
        displayAnimAndChangeTexton(ChangeAnimFactory.createCriticalChangeAnim(anchorPanePres, cc), cc);

    }

    private void installTexton(Texton newTexton) {
        lastTexton = curTexton;
        curTexton = newTexton;
        Timeline blurAnim = blurDuringTransition(anchorPanePres);
        blurAnim.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != Animation.Status.PAUSED) return;
            installTextonInner();
            blurAnim.play();
        });
        blurAnim.play();
    }

    private void installTextonInner() {
        updateFields(curTexton);
        resetTextonClock();
        tcVis.setTexton(curTexton);
        tcTabBord.setTexton(curTexton);
        if (curTexton instanceof TextonV) {
            displayCountdown(((TextonV) curTexton).getTimer());
            timerRunning.set(true);
        }
    }

    private Timeline blurDuringTransition(Pane pane) {
        GaussianBlur effect = new GaussianBlur(0);
        pane.setEffect(effect);
        blurAnim.setCycleCount(2);
        blurAnim.getKeyFrames().add(new KeyFrame(Duration.seconds(2), event -> {
            Util.toggleProperty(transitionPeakFlag);
            blurAnim.pause();
        }, new KeyValue(effect.radiusProperty(), 63)));
        blurAnim.setAutoReverse(true);
        return blurAnim;
    }

    private void doTimerOverAction() {
        StringBuilder sbVotes = new StringBuilder();
        ControlCode maxVotes = pollController.getPollCounter().getMax(sbVotes);
        displayAnimAndChangeTexton(ChangeAnimFactory.createTimerOverAnim(anchorPanePres,
                maxVotes, sbVotes.toString()), maxVotes);
        timerRunning.set(false);
    }

    private void displayAnimAndChangeTexton(ChangeAnim changeAnim, ControlCode cc) {
        changeAnim.isOverProperty().addListener((observable, oldValue, newValue) -> changeTexton(cc));
        changeAnim.play();
    }

    private void updateFields(Texton updTexton) {
        lblNumTexton.setText(Integer.toString(updTexton.getNumTexton()));
        lblType.setText(updTexton.getType().toString());
        textAreaSource.setText(updTexton.getSource());
        textAreaTexte.setText("Nom du texton : " + updTexton.getName() + "\n" + updTexton.getDescription());

    }

    private void installPres() {
        if (isPresInstalled) return;
        ObservableList<Screen> screens = Screen.getScreens();
        //If there is only one screen, action cannot be performed.
        if (screens.size() != 2) {
            FXCustomDialogs.showError("This function needs two screens.");
            return;
        }
        ObservableList<Screen> screensForTab;
        if ((screensForTab = Screen.getScreensForRectangle(stageTableau.getX(), stageTableau.getY(), stageTableau.getWidth(), stageTableau.getHeight())).size() != 1) {
            FXCustomDialogs.showError("Cannot detect position of dashboard.");
            return;
        }
        Screen screenForTab = screensForTab.get(0);
        Screen visScreen = null;
        if (screens.get(0).equals(screenForTab)) {
            visScreen = screens.get(1);
        }
        if (screens.get(1).equals(screenForTab)) {
            visScreen = screens.get(0);
        }
        //Check that screen is big enough
        if (visScreen != null && (visScreen.getBounds().getWidth() < 800 || visScreen.getBounds().getHeight() < 600)) {
            FXCustomDialogs.showError("Visualization screen does have the required resolution of 800x600.");
            return;
        }
        //Move visualization to appropriate screen.
        stageVis.setX(visScreen != null ? visScreen.getVisualBounds().getMinX() : 0);
        stageVis.setY(visScreen != null ? visScreen.getVisualBounds().getMinY() : 0);
        stageVis.setWidth(visScreen != null ? visScreen.getBounds().getWidth() : 0);
        stageVis.setHeight(visScreen != null ? visScreen.getBounds().getHeight() : 0);
        stageVis.toFront();
        stageTableau.setMaximized(true);
        isPresInstalled = true;
    }

    private void restorePres() {
        if (!isPresInstalled) return;
        stageVis.setWidth(800);
        stageVis.setHeight(600);
        isPresInstalled = false;
    }

    @FXML
    private void menuHandler(ActionEvent event) {
        if (event.getSource() == menuFermer) {
            stageVis.close();
            stageTableau.close();
        }
        if (event.getSource() == menuLienA)
            changeTexton(curTexton.getLienTexton().get(0).getNumSerie());
        if (event.getSource() == menuLienB)
            changeTexton(curTexton.getLienTexton().get(1).getNumSerie());
        if (event.getSource() == menuLienC)
            changeTexton(curTexton.getLienTexton().get(2).getNumSerie());
        if (event.getSource() == menuLienD)
            changeTexton(curTexton.getLienTexton().get(3).getNumSerie());
        if (event.getSource() == menuNaviguerAuTexton) {
            String choix = FXCustomDialogs.showInput("Naviguer vers le texton…");
            if (choix != null) {
                int numChoisi = Integer.parseInt(choix);
                if (Integer.parseInt(choix) >= 0 && Integer.parseInt(choix) <= 1000) {
                    try {
                        changeTexton(numChoisi);
                    } catch (Exception e) {
                        FXCustomDialogs.showException(e);
                        e.printStackTrace();
                    }
                }
            }
        }
        if (event.getSource() == menuOuvrirRecital) {
            try {
                sqlConnector = new SqlConnector();
                changeTexton(1);
                startRecitalTimer();
            } catch (Exception e) {
                FXCustomDialogs.showException(e);
                e.printStackTrace();
            }
        }
        if (event.getSource() == menuInstaller) {
            installPres();
        }
        if (event.getSource() == menuRestaurer) {
            restorePres();
        }
        if (event.getSource() == menuPrecedent) {
            if (lastTexton != null) {
                installTexton(lastTexton);
            }
        }
    }

    @FXML
    void handleButtonAction(ActionEvent event) {
        if (event.getSource() == btnTermine) {
            if (curTexton == null) return;
            changeTexton();
        }
    }

    @FXML
    void initialize() {
        pollContTask = pollController.getPollContTask();
        pollContTask.messageProperty().addListener(pollThreadListener);

        if (!isInitialised) {
            propA = new SimpleIntegerProperty();
            propB = new SimpleIntegerProperty();
            propC = new SimpleIntegerProperty();
            propD = new SimpleIntegerProperty();
            propNumEnr = new SimpleIntegerProperty();
            propTimer = new SimpleIntegerProperty();
        }

        if (isInitialised) {
            textAreaTexte.setWrapText(true);
            textAreaSource.setWrapText(true);
            tglSeuil.selectedProperty().addListener(changeListenerSelectedProperty);
            lblNumA.textProperty().bind(propA.asString());
            lblNumB.textProperty().bind(propB.asString());
            lblNumC.textProperty().bind(propC.asString());
            lblNumD.textProperty().bind(propD.asString());
            lblNumEnregistres.textProperty().bind(propNumEnr.asString());
            //TV Timer label is controlled by propTimer.
            propTimer.addListener((observable, oldValue, newValue) ->
                    lblTimer.textProperty().set(getFormattedTime(newValue.intValue())));

            lblSeuilPres.textProperty().set("0");
            propNumEnr.addListener((observable, oldValue, newValue) -> {
                String correctedProportion = String.valueOf((int) Math.ceil(newValue.doubleValue() * (double) PollCounter.getCriticalProportion() / 100d));
                lblSeuilPres.textProperty().set(correctedProportion);
                lblNumSeuil.textProperty().set(correctedProportion);
            });

            lblVotesEnregistresVis.textProperty().bind(propA.add(propB.add(propC.add(propD))).asString());

            lblTotalVotesVis.textProperty().bind(propNumEnr.asString());

            lblNumAVis.textProperty().bind(propA.asString());
            lblNumBVis.textProperty().bind(propB.asString());
            lblNumCVis.textProperty().bind(propC.asString());
            lblNumDVis.textProperty().bind(propD.asString());
            lblSeuilPresText.visibleProperty().bindBidirectional(lblSeuilPres.visibleProperty());
            lblSeuilPres.visibleProperty().set(false);
            lblTimer.disableProperty().bind(timerRunning.not());

            /*tcVis = new TextonImageCanvas();
            tcDessinVis = new TextonOverlayCanvas();
            tcTabBord = new TextonImageCanvas();
            tcDessinTabBord = new TextonOverlayCanvas();
            */
            tcVis = new CompositeTextonCanvas();
            tcTabBord = new CompositeTextonCanvas();

            CanvasUtil.setNodeAnchorToAnchorPane(tcVis, 0, 40, 0, 0);
            //CanvasUtil.setNodeAnchorToAnchorPane(tcDessinVis, 0, 40, 0, 0);
            CanvasUtil.setNodeAnchorToAnchorPane(tcTabBord, 0, 0, 0, 0);
            //CanvasUtil.setNodeAnchorToAnchorPane(tcDessinTabBord, 0, 0, 0, 0);

            //anchorPanePres.getChildren().addAll(tcVis, tcDessinVis);
            //anchorPaneTabBord.getChildren().addAll(tcTabBord, tcDessinTabBord);

            anchorPanePres.getChildren().add(tcVis);
            anchorPaneTabBord.getChildren().add(tcTabBord);

            ResizableDraggableNodeManager.makeNodeDraggable(anchorPanePres);
            ResizableDraggableNodeManager.makeNodeResizableCtrl(anchorPanePres);

            setupClocks();

            //For testing only
            tBarPres.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    criticalChangeTexton(ControlCode.A);
                } else {
                    doTimerOverAction();
                }
            });
        }
        isInitialised = true;
    }

    private void displayCountdown(int timer) {
        CounterAnim counterAnim = CounterFactory.createCinemaCounter(timer, anchorPanePres.getWidth() / 8);
        //La prochaine ligne sert uniquement aux tests. Pour l'implémentation, utiliser la ligne ci-haut.
        //CounterAnim counterAnim = CounterFactory.createCinemaCounter(5, anchorPanePres.getWidth() / 8);
        cCounter = counterAnim.getNode();
        double cCounterOffset = anchorPanePres.getWidth() / 80;
        cCounter.setLayoutX(anchorPanePres.getWidth() - counterAnim.getSize() - cCounterOffset);
        cCounter.setLayoutY(anchorPanePres.getHeight() - counterAnim.getSize() - tBarPres.getHeight() - cCounterOffset);
        anchorPanePres.getChildren().add(cCounter);
        counterAnim.counterProperty().addListener(countDownListener);
        counterAnim.isOverProperty().addListener(countDownOverListener);
        counterAnim.play();
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

    private Text getCriticalPropVisText() {
        Text text = new Text(stageVis.getWidth() - 260, stageVis.getHeight() - 40, "Seuil critique activé!");
        text.setFont(new Font("Times New Roman", 30));
        text.setFill(Color.RED);
        return text;
    }

    void setStageTableau(Stage stageTableau) {
        this.stageTableau = stageTableau;
    }

    void setStageVis(Stage stageVis) {
        this.stageVis = stageVis;
    }

    public Texton getCurTexton() {
        return curTexton;
    }
}
