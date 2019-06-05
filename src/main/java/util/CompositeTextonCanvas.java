package util;

import javafx.scene.layout.AnchorPane;
import textonclasses.Graph;
import textonclasses.Texton;

import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-01-05.
 */
public class CompositeTextonCanvas extends AnchorPane implements TCWithTexton {

    private Graph graph;
    private final TextonImageCanvas textonImageCanvas = new TextonImageCanvas();
    private final TextonOverlayCanvas textonOverlayCanvas = new TextonOverlayCanvasOnlyText();

    {
        getChildren().addAll(textonImageCanvas, textonOverlayCanvas);
        getChildren().forEach(node -> CanvasUtil.setNodeAnchorToAnchorPane(node, 0, 0, 0, 0));
    }

    @Override
    public Texton getTexton() {
        return textonImageCanvas.getTexton();
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        Stream.of(textonImageCanvas, textonOverlayCanvas).forEach(node -> node.setGraph(graph));
    }

    @Override
    public void setTexton(Texton texton) {
        textonImageCanvas.setTexton(texton);
        textonOverlayCanvas.setTexton(texton);
    }
}
