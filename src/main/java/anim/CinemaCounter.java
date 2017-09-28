package anim;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import util.CanvasUtil;

/**
 * Created by Mikaël on 2016-12-21.
 */
class CinemaCounter implements CounterAnim {
    final private double SIZE;
    final private double CENTERX;
    final private double CENTERY;
    final private double LINEWIDTH;
    final private double PULSEANIMCOUNTERINIT = 1.5;  //Initial scale of text during pulses.
    private final StackPane root = new StackPane();
    private IntegerProperty counter;
    private Arc innerFill;
    private Arc innerTransitionFill;
    private Text text;
    private int colorIndex = 2;
    private final Timeline timeline = new Timeline();
    private Timeline timelinePulse;
    private double pulseAnimCounter = PULSEANIMCOUNTERINIT;
    private final BooleanProperty isOver = new SimpleBooleanProperty(false);

    CinemaCounter(int counterInit, double size) {
        if (counterInit <= 0) throw new IllegalArgumentException("Initial counter has to be over 0.");
        if (size <= 0) throw new IllegalArgumentException("Size factor has to be over 0.");
        SIZE = size;
        CENTERX = SIZE;
        CENTERY = SIZE;
        LINEWIDTH = SIZE / 70;
        counter = new SimpleIntegerProperty(counterInit);
        counter.addListener((observable, oldValue, newValue) -> text.setText(String.valueOf(newValue)));
        innerFill = innerFill();
        innerTransitionFill = innerTransitionFill();
        Group group = new Group();
        group.getChildren().addAll(innerTransitionFill, innerFill, innerCross(), outerCircle());
        root.getChildren().addAll(group, text());
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(new Duration(10), event -> animateTimer()));
        timelinePulse = new Timeline();
        timelinePulse.setCycleCount(40);
        timelinePulse.getKeyFrames().add(new KeyFrame(new Duration(5), event -> {
            pulseText(pulseAnimCounter);
            pulseAnimCounter -= 0.0075;
        }));
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    public void play() {
        isOver.set(false);
        timeline.play();
    }

    private void animateTimer() {
        double length = innerFill.getLength();
        innerFill.setLength(length + 3.6);
        if (length >= 360) {
            //Actions on counter change
            pulseAnimCounter = PULSEANIMCOUNTERINIT;
            timelinePulse.play();
            counter.set(counter.get() - 1);
            innerFill.setLength(length - 360);
            innerTransitionFill.setFill(innerFill.getFill());
            if (colorIndex == 4) colorIndex = 0;
            innerFill.setFill(CanvasUtil.getLinkToColorMap().get(colorIndex++));
        }
        if (counter.get() == -1) {
            //When counter is finished
            counter.set(0);
            timeline.stop();
            isOver.set(true);
        }
    }

    private void pulseText(double temp) {
        text.getTransforms().clear();
        text.setScaleX(temp);
        text.setScaleY(temp);
    }

    private Arc innerTransitionFill() {
        Arc arc = new Arc();
        arc.setFill(CanvasUtil.getLinkToColorMap().get(0));
        arc.setCenterX(CENTERX);
        arc.setCenterY(CENTERY);
        arc.setRadiusX(SIZE / 2);
        arc.setRadiusY(SIZE / 2);
        arc.setLength(360);
        arc.setType(ArcType.ROUND);
        return arc;
    }

    private Arc innerFill() {
        Arc arc = new Arc();
        arc.setFill(CanvasUtil.getLinkToColorMap().get(1));
        arc.setCenterX(CENTERX);
        arc.setCenterY(CENTERY);
        arc.setRadiusX(SIZE / 2);
        arc.setRadiusY(SIZE / 2);
        arc.setLength(0);
        arc.setType(ArcType.ROUND);
        arc.setStartAngle(90);
        return arc;
    }

    private Node outerCircle() {
        Circle circle = new Circle();
        circle.setStrokeWidth(LINEWIDTH);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setCenterX(CENTERX);
        circle.setCenterY(CENTERY);
        circle.setRadius(SIZE / 2);
        return circle;
    }

    private Node innerCross() {
        Line lineH = new Line(CENTERX - SIZE / 2, CENTERY, CENTERX + SIZE / 2, CENTERY);
        lineH.setStrokeWidth(2);
        Line lineV = new Line(CENTERX, CENTERY - SIZE / 2, CENTERX, CENTERY + SIZE / 2);
        lineV.setStrokeWidth(2);
        return new Group(lineH, lineV);
    }

    private Node text() {
        text = new Text(0, 0, String.valueOf(counter.get()));
        text.setFont(new Font("Times New Roman", SIZE / 3));
        text.setTextAlignment(TextAlignment.CENTER);
        return text;
    }

    @Override
    public BooleanProperty isOverProperty() {
        return isOver;
    }

    @Override
    public double getSize() {
        return SIZE;
    }

    @Override
    public IntegerProperty counterProperty() {
        return counter;
    }
}