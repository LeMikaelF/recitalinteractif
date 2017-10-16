package util;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.scene.layout.AnchorPane;
import textonclasses.Graph;
import textonclasses.Texton;

import java.util.stream.Stream;

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
        Stream.of(textonImageCanvas, textonOverlayCanvas).forEach(node -> node.setGraph(graph));
    }

    @Override
    public void setTexton(Texton texton) {
        textonImageCanvas.setTexton(texton);
        textonOverlayCanvas.setTexton(texton);
    }
}
