package util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
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

    public static Map<Integer, Color> getLinkToColorMap() {
        return linkToColorMap;
    }

    public static void setNodeAnchorToAnchorPane(Node node, double top, double bottom, double left, double right) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);
    }


}
