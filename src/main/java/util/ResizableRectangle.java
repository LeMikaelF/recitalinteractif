package util;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Mikaël on 2017-01-05.
 */
public class ResizableRectangle extends Group {
    private Circle[] circleHandles = new Circle[8];
    private Rectangle rectangle = new Rectangle();
    private Point2D mouseDownPoint = new Point2D(0, 0);
    private DoubleProperty mouseX = new SimpleDoubleProperty(0);
    private DoubleProperty mouseY = new SimpleDoubleProperty(0);
    private DoubleProperty deltaX = new SimpleDoubleProperty();
    private DoubleProperty deltaY = new SimpleDoubleProperty();
    private DoubleProperty[] coords = new DoubleProperty[4];
    private double[] coordsAtMouseDown = new double[4];
    private boolean boundsSpecified = false;
    private DoubleProperty boundMinX = new SimpleDoubleProperty();
    private DoubleProperty boundMinY = new SimpleDoubleProperty();
    private DoubleProperty boundMaxX = new SimpleDoubleProperty();
    private DoubleProperty boundMaxY = new SimpleDoubleProperty();

    private EventHandler<MouseEvent> mouseDownHandler = event -> {
        mouseDownPoint = new Point2D(event.getX(), event.getY());

        Integer target = findTarget();
        mouseX.set(event.getX());
        mouseY.set(event.getY());
        bindMouseDelta();

        for (int i = 0; i < coords.length; i++) {
            coordsAtMouseDown[i] = coords[i].get();
        }

        if (targetIsHandle()) {
            if (new ArrayList<>(Arrays.asList(new Integer[]{0, 6, 7})).contains(target))
                if (boundsSpecified)
                    coords[0].bind(clampBinding(Bindings.add(coordsAtMouseDown[0], deltaX), boundMinX.get(), boundMaxX.get()));
                else coords[0].bind(Bindings.add(coordsAtMouseDown[0], deltaX));
            if (new ArrayList<>(Arrays.asList(new Integer[]{2, 3, 4})).contains(target))
                if (boundsSpecified)
                    coords[2].bind(clampBinding(Bindings.add(coordsAtMouseDown[2], deltaX), boundMinX.get(), boundMaxX.get()));
                else coords[2].bind(Bindings.add(coordsAtMouseDown[2], deltaX));
            if (new ArrayList<>(Arrays.asList(new Integer[]{0, 1, 2})).contains(target))
                if (boundsSpecified)
                    coords[1].bind(clampBinding(Bindings.add(coordsAtMouseDown[1], deltaY), boundMinY.get(), boundMaxY.get()));
                else coords[1].bind(Bindings.add(coordsAtMouseDown[1], deltaY));
            if (new ArrayList<>(Arrays.asList(new Integer[]{4, 5, 6})).contains(target))
                if (boundsSpecified)
                    coords[3].bind(clampBinding(Bindings.add(coordsAtMouseDown[3], deltaY), boundMinY.get(), boundMaxY.get()));
                else coords[3].bind(Bindings.add(coordsAtMouseDown[3], deltaY));
        } else {
            coords[0].bind(clampBinding(Bindings.add(coordsAtMouseDown[0], deltaX), boundMinX.get(), boundMaxX.get()));
            coords[2].bind(clampBinding(Bindings.add(coordsAtMouseDown[2], deltaX), boundMinX.get(), boundMaxX.get()));
            coords[1].bind(clampBinding(Bindings.add(coordsAtMouseDown[1], deltaY), boundMinY.get(), boundMaxY.get()));
            coords[3].bind(clampBinding(Bindings.add(coordsAtMouseDown[3], deltaY), boundMinY.get(), boundMaxY.get()));
        }
    };
    private EventHandler<MouseEvent> mouseDragHandler = event -> {
        mouseX.set(event.getX());
        mouseY.set(event.getY());
    };
    private EventHandler<MouseEvent> mouseUpHandler = event -> {
        unBindMouseDelta();
        for (DoubleProperty prop : coords) {
            prop.unbind();
        }
        deltaX.set(0);
        deltaY.set(0);
    };

    //Initialization block
    {
        int HANDLERADIUS = 5;
        IntStream.range(0, circleHandles.length).forEach(i -> circleHandles[i] = new Circle(HANDLERADIUS, Color.BLACK));
        IntStream.range(0, coords.length).forEach(i -> coords[i] = new SimpleDoubleProperty(0));

        getChildren().add(rectangle);
        getChildren().addAll(circleHandles);

        addEventHandler(MouseEvent.MOUSE_PRESSED, mouseDownHandler);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
        addEventHandler(MouseEvent.MOUSE_RELEASED, mouseUpHandler);

        //Set relative positions of handles
        circleHandles[0].centerXProperty().bind(coords[0]);
        circleHandles[0].centerYProperty().bind(coords[1]);
        circleHandles[1].centerXProperty().bind(Bindings.add(Bindings.divide(Bindings.subtract(coords[2], coords[0]), 2), coords[0]));
        circleHandles[1].centerYProperty().bind(coords[1]);
        circleHandles[2].centerXProperty().bind(coords[2]);
        circleHandles[2].centerYProperty().bind(coords[1]);
        circleHandles[3].centerXProperty().bind(coords[2]);
        circleHandles[3].centerYProperty().bind(Bindings.add(Bindings.divide(Bindings.subtract(coords[3], coords[1]), 2), coords[1]));
        circleHandles[4].centerXProperty().bind(coords[2]);
        circleHandles[4].centerYProperty().bind(coords[3]);
        circleHandles[5].centerXProperty().bind(Bindings.add(Bindings.divide(Bindings.subtract(coords[2], coords[0]), 2), coords[0]));
        circleHandles[5].centerYProperty().bind(coords[3]);
        circleHandles[6].centerXProperty().bind(coords[0]);
        circleHandles[6].centerYProperty().bind(coords[3]);
        circleHandles[7].centerXProperty().bind(coords[0]);
        circleHandles[7].centerYProperty().bind(Bindings.add(Bindings.divide(Bindings.subtract(coords[3], coords[1]), 2), coords[1]));

        rectangle.xProperty().bind(coords[0]);
        rectangle.yProperty().bind(coords[1]);
        rectangle.widthProperty().bind(Bindings.subtract(coords[2], coords[0]));
        rectangle.heightProperty().bind(Bindings.subtract(coords[3], coords[1]));
    }

    //Constructor
    ResizableRectangle(Rectangle2D pos, Color outline) {
        moveRectangle(pos);
        rectangle.setStroke(outline);
        rectangle.setFill(outline.interpolate(Color.TRANSPARENT, 0.9));
        for (Circle circleHandle : circleHandles) {
            circleHandle.setFill(outline);
        }
    }

    public void moveRectangle(Rectangle2D pos) {
        coords[0].set(pos.getMinX());
        coords[1].set(pos.getMinY());
        coords[2].set(pos.getMinX() + pos.getWidth());
        coords[3].set(pos.getMinY() + pos.getHeight());
    }

    public static List<Double> rect2dAsList(Rectangle2D rect) {
        List<Double> list = new ArrayList<>();
        list.add(rect.getMinX());
        list.add(rect.getMinY());
        list.add(rect.getMaxX());
        list.add(rect.getMaxY());
        return list;
    }

    private ObservableNumberValue clampBinding(ObservableNumberValue obs, double low, double high) {
        return Bindings.when(Bindings.lessThan(obs, low)).then(low).otherwise(Bindings.when(Bindings
                .greaterThan(obs, high)).then(high).otherwise(obs));
    }

    void fitInBounds(SimpleListProperty<Double> bounds) {
        boundsSpecified = true;
        bounds.addListener((observable, oldValue, newValue) -> {
            boundMinX.set(newValue.get(0));
            boundMinY.set(newValue.get(1));
            boundMaxX.set(newValue.get(2));
            boundMaxY.set(newValue.get(3));
        });
    }

    Rectangle2D asRectangle2D() {
        return new Rectangle2D(coords[0].get(), coords[1].get(), coords[2].get() - coords[0].get(), coords[3].get() - coords[1].get());
    }

    private void bindMouseDelta() {
        System.out.println("Bindings mouse delta : mouseX = " + mouseX.get() + "\t mouseDownPoint.getX() = " + mouseDownPoint.getX());
        deltaX.bind(Bindings.subtract(mouseX, mouseDownPoint.getX()));
        deltaY.bind(Bindings.subtract(mouseY, mouseDownPoint.getY()));
    }

    private void unBindMouseDelta() {
        deltaX.unbind();
        deltaY.unbind();
    }

    private boolean targetIsHandle() {
        for (Circle circleHandle : circleHandles) {
            if (circleHandle.contains(mouseDownPoint))
                return true;
        }
        return false;
    }

    private Integer findTarget() {
        Integer targetHandle = null;
        for (int i = 0; i < circleHandles.length; i++) {
            if (circleHandles[i].contains(mouseDownPoint))
                targetHandle = i;
        }
        if (targetHandle != null) {
            return targetHandle;
        } else return null;
    }
}
