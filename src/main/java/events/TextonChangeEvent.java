package events;

import textonclasses.Texton;
import textonclasses.Graph;

/**
 * Created by Mikaël on 2017-09-29.
 */
public class TextonChangeEvent{
    private final Texton texton;
    private final Graph graph;

    public Texton getTexton() {
        return texton;
    }

    public Graph getGraph() {
        return graph;
    }

    public TextonChangeEvent(Texton texton, Graph graph) {

        this.texton = texton;
        this.graph = graph;
    }
}
