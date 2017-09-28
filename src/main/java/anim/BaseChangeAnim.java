package anim;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server.ControlCode;
import util.CanvasUtil;
import util.Util;

/**
 * Created by Mikaël on 2016-12-30.
 */

abstract class BaseChangeAnim implements ChangeAnim {

    private final double width;
    private final double height;
    private final Rectangle rectangle = new Rectangle();
    private final Text[] text = new Text[2];
    private final String[] textString = {"", "Chargement…"};
    private final Text lienText = new Text();
    private final String idLien;
    private final Text idLienText = new Text();
    private final ProgressBar progressBar = new ProgressBar();
    private final SequentialTransition sequentialTransition = new SequentialTransition();
    private final BooleanProperty isOverProperty = new SimpleBooleanProperty(false);
    private final HBox lienHBox = new HBox(lienText, idLienText);
    private final ControlCode controlCode;
    private final FlowPane flowPane = new FlowPane();
    private final Group group = new Group();
    private boolean rootCreated = false;
    private final Pane pane;

    final private int loadTimeMillis;

    BaseChangeAnim(Pane pane, ControlCode lien, String title, int loadTimeMillis) {
        this.pane = pane;
        width = pane.getWidth();
        height = pane.getHeight();
        this.idLien = lien.toString();
        controlCode = lien;
        textString[0] = title;
        this.loadTimeMillis = loadTimeMillis;

        rectangle.setWidth(width);
        rectangle.setHeight(height);

        progressBar.setVisible(false);
        progressBar.setScaleX(width / 400);
        progressBar.setScaleY(height / 300);
        lienHBox.setAlignment(Pos.CENTER);

        createNodes();
        createTransition();

    }

    @Override
    public Node getNode() {
        if (!rootCreated) {
            createRoot();
        }
        return group;
    }

    @Override
    public void play() {
        pane.getChildren().add(getNode());
        System.out.println("Playing animation.");
        group.setVisible(true);
        sequentialTransition.play();
    }

    @Override
    public BooleanProperty isOverProperty() {
        return isOverProperty;
    }

    void appendToLienXXLine(Node node) {
        lienHBox.getChildren().add(node);
    }

    private void createRoot() {
        rootCreated = true;
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setPrefWrapLength(height);
        flowPane.setPrefWidth(width);
        flowPane.setPrefHeight(height);

        GridPane gp = new GridPane();
        setGpThreeConstraints(gp, text[0], 0, 0, HPos.CENTER);
        setGpThreeConstraints(gp, lienHBox, 1, 0, HPos.CENTER);
        setGpThreeConstraints(gp, text[1], 2, 0, HPos.CENTER);
        setGpThreeConstraints(gp, progressBar, 3, 0, HPos.CENTER);

        gp.setPrefSize(width, height);
        gp.setMinSize(width, height);
        gp.setAlignment(Pos.CENTER);

        RowConstraints rowConstraints = new RowConstraints(height / 4, height / 4, height / 4);
        gp.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints, rowConstraints);

        group.getChildren().addAll(rectangle, gp);
        group.setVisible(false);
    }

    private void setGpThreeConstraints(GridPane gp, Node node, int rowIndex, int columnIndex, HPos hAlignment) {
        GridPane.setRowIndex(node, rowIndex);
        GridPane.setColumnIndex(node, columnIndex);
        GridPane.setHalignment(node, hAlignment);
        gp.getChildren().add(node);
    }

    void setTextStyle(Text text, String str) {
        text.setText(str);
        text.setFont(Font.font("Times New Roman", FontWeight.BOLD, width / 17));
        text.setFill(Color.LEMONCHIFFON);

        DropShadow shadow = new DropShadow();
        shadow.setSpread(0.7);
        text.setEffect(shadow);
    }

    private void createNodes() {
        for (int i = 0; i < text.length; i++) {
            text[i] = new Text();
            setTextStyle(text[i], textString[i]);
        }
        setTextStyle(idLienText, idLien);
        String lienString = "Lien ";
        setTextStyle(lienText, lienString);

        idLienText.setFill(CanvasUtil.getLinkToColorMap().get(Util.ccToInt(controlCode)));

    }

    private void createTransition() {
        FillTransition rectFade = new FillTransition(Duration.seconds(1), rectangle, Color.TRANSPARENT, Color.TRANSPARENT.interpolate(Color.WHITE, 0.5));
        FadeTransition[] textFade = new FadeTransition[text.length];
        for (int i = 0; i < textFade.length; i++) {
            textFade[i] = setupFadeTransition(text[i]);
        }
        FadeTransition hBoxFade = setupFadeTransition(lienHBox);

        sequentialTransition.getChildren().addAll(rectFade, textFade[0], hBoxFade, textFade[1]);
        sequentialTransition.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Animation.Status.STOPPED)) {
                animProgress();
            }
        });
    }

    private FadeTransition setupFadeTransition(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(1000), node);
        fade.setFromValue(0);
        fade.setToValue(0.9);
        return fade;
    }

    private void animProgress() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                final int INCREMENTS = 150;
                //Loading time (in ms)
                for (int i = 0; i < INCREMENTS; i++) {
                    Thread.sleep(loadTimeMillis / INCREMENTS);
                    updateProgress(i, INCREMENTS);
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            ((Pane) getNode().getParent()).getChildren().remove(getNode());
            isOverProperty.set(true);
        });
        progressBar.indeterminateProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) progressBar.setVisible(true);
        });
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

}
