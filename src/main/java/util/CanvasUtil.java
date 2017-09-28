package util;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import textonclasses.LienTexton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mikaël on 2016-11-13.
 */
public class CanvasUtil {

    private static final Map<Integer, Color> linkToColorMap;

    static {
        linkToColorMap = new HashMap<>();
        linkToColorMap.put(0, Color.web("#428bca"));
        linkToColorMap.put(1, Color.web("#d9534f"));
        linkToColorMap.put(2, Color.web("#f0ad4e"));
        linkToColorMap.put(3, Color.web("#5cb85c"));
    }

    public static void drawLinkOnCanvas(Rectangle2D rect, GraphicsContext gc, Paint paint, Rectangle2D offset) {
        System.out.println("Methode drawLinkOnCanvas");
        gc.setLineWidth(5);
        gc.setStroke(paint);
        gc.setEffect(new Bloom(0.1));
        Rectangle2D newRect;
        if (offset == null) {
            newRect = percentRectToRealCoords(rect, gc.getCanvas().widthProperty().get(), gc.getCanvas().heightProperty().get());
            gc.strokeRect(newRect.getMinX(), newRect.getMinY(), newRect.getWidth(), newRect.getHeight());
        } else {
            newRect = percentRectToRealCoords(rect, offset.getWidth(), offset.getHeight());
            gc.strokeRect(newRect.getMinX() + offset.getMinX(), newRect.getMinY() + offset.getMinY(), newRect.getWidth(), newRect.getHeight());
        }
    }

    public static void drawLinksAndNumOnTMCanvas(List<LienTexton> lien, GraphicsContext gc, Paint paint) {
        System.out.println("Method drawLinkAndNumOnTMCanvas");
        gc.setLineWidth(5);
        gc.setStroke(paint);

        double height = gc.getCanvas().getHeight();

        for (int i = 0; i < lien.size(); i++) {
            gc.setFont(new Font("Times New Roman", gc.getCanvas().getWidth() / 20));
            gc.setFill(Color.WHITE);
            gc.setLineWidth(2);
            DropShadow dropShadow = new DropShadow();
            dropShadow.setSpread(0.7);
            gc.setEffect(dropShadow);
            gc.fillText(lien.get(i).getName() != null ? lien.get(i).getName() : "Sans titre", 40, height * (i + 1) / 4 - height / 8, gc.getCanvas().getWidth());
        }
    }

    public static Map<Integer, Color> getLinkToColorMap() {
        return linkToColorMap;
    }

    public static Rectangle2D percentRectToRealCoords(Rectangle2D percentRect, double width, double height) {
        System.out.println("Coordonnées du rectangle : (" + percentRect.getMinX() + " , " + percentRect.getMinY() + ") , ("
                + percentRect.getMaxX() + " , " + percentRect.getMaxY() + ")");
        double newMinX = Util.clamp(0d, width, percentRect.getMinX() * width / 100d);
        double newMinY = Util.clamp(0d, height, percentRect.getMinY() * height / 100d);
        double newWidth = Util.clamp(0d, width, percentRect.getWidth() * width / 100d);
        double newHeight = Util.clamp(0d, height, percentRect.getHeight() * height / 100d);
        System.out.println("MinX = " + newMinX + "\tMinY = " + newMinY + "\twidth = " + newWidth + "\theight = " + newHeight);
        Rectangle2D result = new Rectangle2D(newMinX, newMinY, newWidth, newHeight);
        return result;
    }

    public static Rectangle2D realCoordsToPercentRect(Rectangle2D rect, Rectangle2D bounds) {
        double newMinX = Util.clamp(0d, 100d, (rect.getMinX() - bounds.getMinX()) * 100 / bounds.getWidth());
        double newMinY = Util.clamp(0d, 100d, (rect.getMinY() - bounds.getMinY()) * 100 / bounds.getHeight());
        double newWidth = Util.clamp(0d, 100d, rect.getWidth() * 100 / bounds.getWidth());
        double newHeight = Util.clamp(0d, 100d, rect.getHeight() * 100 / bounds.getHeight());
        return new Rectangle2D(newMinX, newMinY, newWidth, newHeight);
    }

    public static void setNodeAnchorToAnchorPane(Node node, double top, double bottom, double left, double right) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);
    }

}
