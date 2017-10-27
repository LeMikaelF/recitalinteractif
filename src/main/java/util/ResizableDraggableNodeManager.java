package util;

import javafx.scene.Node;
import javafx.stage.Window;

import java.util.HashMap;

/**
 * Created by Mikaël on 2017-01-01.
 */
public class ResizableDraggableNodeManager {

    private static final HashMap<Node, Double[]> nodeToCoord;

    static {
        nodeToCoord = new HashMap<>();
    }

    private ResizableDraggableNodeManager() {}

    public static void makeNodeResizableCtrl(Node node) {
        node.setOnScroll(event -> {
            event.consume();
            final double scrConst = 1.05;
            double scroll = event.getDeltaY();
            if (scroll == 0) return;
            node.getScene().getWindow().setWidth(node.getScene().getWindow().getWidth() * ((scroll > 0) ? scrConst : 1 / scrConst));
            node.getScene().getWindow().setHeight(node.getScene().getWindow().getHeight() * ((scroll > 0) ? scrConst : 1 / scrConst));
        });
    }

    public static void makeNodeDraggable(Node node) {
        node.setOnMousePressed(event -> {
            event.consume();
            nodeToCoord.put(node, new Double[]{event.getScreenX(), event.getScreenY()});
        });

        node.setOnMouseDragged(event -> {
            event.consume();
            double initX = nodeToCoord.get(node)[0];
            double initY = nodeToCoord.get(node)[1];
            Window window = node.getScene().getWindow();
            window.setX(window.getX() + (event.getScreenX() - initX));
            window.setY(window.getY() + (event.getScreenY() - initY));
            nodeToCoord.put(node, new Double[]{event.getScreenX(), event.getScreenY()});
        });
    }
}
