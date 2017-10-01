package events;

import textonclasses.Texton;
import util.Graph;

/**
 * Created by Mikaël on 2017-09-29.
 */
public class TextonChangeEvent{
    final private Texton texton;
    final private Graph graph;

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
