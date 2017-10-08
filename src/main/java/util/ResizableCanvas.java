package util;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.Effect;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Mikaël on 2016-12-02.
 */
/*L'extension de la classe Canvas a été initialement proposée par Lemmerman, Dirk. « JavaFX Tip 1 : Resizable Canvas ».
Dirk Lemmerman Software & Consulting. 10 avril 2014.
<http://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/>. Consulté le 2 décembre 2016.*/

abstract class ResizableCanvas extends Canvas {

    private double lastWidth = 0;
    private double lastHeight = 0;
    private ObservableList<Double> observableList = FXCollections.observableArrayList(new ArrayList<Double>());
    private SimpleListProperty<Double> boundsProperty = new SimpleListProperty<>(observableList);

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public void resize(double width, double height) {
        boolean setAndQuit = false;
        if (lastWidth == width && lastHeight == height) return;
        lastWidth = width;
        lastHeight = height;
        setWidth(width);
        setHeight(height);
        draw();
    }

    void fitImage(BufferedImage image) {
        Rectangle2D rect = getFittedCoords(image);
        boundsProperty.get().setAll(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY());
        getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(image, null), rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    Rectangle2D getFittedCoords(BufferedImage image) {
        /* Cette section est adaptée de Ali, Sadique. « Fitting an Image in to [sic] a Canvas Object ». Code that Works.
         * 3 octobre 2013. <https://sdqali.in/blog/2013/10/03/fitting-an-image-in-to-a-canvas-object/>. Consulté le
         * 2 décembre 2016.
         */
        double imgAspectRatio = image.getWidth() / image.getHeight();
        double canvasAspectRatio = getWidth() / getHeight();
        double renderableWidth, renderableHeight, xStart, yStart;
        double width = getWidth();
        double height = getHeight();

        if (imgAspectRatio < canvasAspectRatio) {
            renderableHeight = height;
            renderableWidth = image.getWidth() * (renderableHeight / image.getHeight());
            xStart = (width - renderableWidth) / 2;
            yStart = 0;
        } else if (imgAspectRatio > canvasAspectRatio) {
            renderableWidth = width;
            renderableHeight = image.getHeight() * (renderableWidth / image.getWidth());
            xStart = 0;
            yStart = (height - renderableHeight) / 2;
        } else {
            renderableHeight = height;
            renderableWidth = width;
            xStart = 0;
            yStart = 0;
        }
        /* Fin de l'emprunt à Ali */

        return new Rectangle2D(xStart, yStart, renderableWidth, renderableHeight);
    }

    void clearCanvas() {
        Effect effect = getGraphicsContext2D().getEffect(null);
        getGraphicsContext2D().setEffect(null);
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        getGraphicsContext2D().setEffect(effect);
    }

    protected abstract void draw();

}
