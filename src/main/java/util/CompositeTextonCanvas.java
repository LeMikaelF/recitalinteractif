package util;

import javafx.scene.layout.AnchorPane;
import textonclasses.Texton;

/**
 * Created by Mikaël on 2017-01-05.
 */
public class CompositeTextonCanvas extends AnchorPane implements TCWithTexton {

    private Graph graph;
    private TextonImageCanvas textonImageCanvas = new TextonImageCanvas();
    private TextonOverlayCanvas textonOverlayCanvas = new TextonOverlayCanvasOnlyText();

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
    }

    @Override
    public void setTexton(Texton texton) {

        textonImageCanvas.setTexton(texton);
        textonOverlayCanvas.setTexton(texton);
    }
}
