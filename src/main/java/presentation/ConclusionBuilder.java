package presentation;

import javafx.scene.Node;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.util.List;

/**
 * Created by Mikaël on 2017-10-21.
 */
public abstract class ConclusionBuilder {
    private final Graph graph;
    private final List<TextonHeader> path;

    ConclusionBuilder(List<TextonHeader> path, Graph graph) {
        this.path = path;
        this.graph = graph;
    }

    List<TextonHeader> getPath() {
        return path;
    }

    Graph getGraph() {
        return graph;
    }

    public abstract Node getNode();
}
