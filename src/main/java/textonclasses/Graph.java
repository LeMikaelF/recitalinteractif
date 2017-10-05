package textonclasses;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mikaël on 2017-09-28.
 */
public class Graph {
    final private Map<Integer, String> nodes;
    final private List<TextonLien> edges;

    public Graph(Map<Integer, String> nodes, List<TextonLien> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public String getName(int numTexton){
        return nodes.get(numTexton);
    }

    public List<Integer> getChildren(int numTexton) {
        return edges.stream().
                filter(textonLien -> textonLien.getFrom() == numTexton).
                mapToInt(TextonLien::getTo).boxed().collect(Collectors.toList());
    }

    public List<TextonLien> getEdges() {
        return Collections.unmodifiableList(edges);
    }
}
