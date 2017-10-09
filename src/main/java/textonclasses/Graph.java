package textonclasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.json.Json;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mikaël on 2017-09-28.
 */

public class Graph {

    final private List<TextonHeader> nodes;
    final private List<TextonLien> edges;

    @JsonCreator
    public Graph(@JsonProperty("nodes") List<TextonHeader> nodes, @JsonProperty("edges") List<TextonLien> edges) {
        this.nodes = nodes;
        nodes.add(0, new TextonHeader(0, "Texton 0"));
        this.edges = edges;
    }

    public static Graph createGraph(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Graph.class);
    }

    public List<TextonHeader> getNodes() {
        return nodes;
    }

    public List<TextonLien> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public String getName(int numTexton) {
        return nodes.get(numTexton).getName();
    }

    public TextonHeader getTextonHeader(int numTexton) {
        List<TextonHeader> list = nodes.stream().filter(textonHeader -> textonHeader.getNumTexton() == numTexton)
                .limit(1).collect(Collectors.toList());
        return list.get(0);
    }

    public List<Integer> getChildren(int numTexton) {
        return edges.stream().
                filter(textonLien -> textonLien.getFrom() == numTexton)
                .mapToInt(TextonLien::getTo).boxed().collect(Collectors.toList());
    }

    public List<TextonHeader> getTextonHeaderChildren(int numTexton) {
        return edges.stream().filter(textonLien -> textonLien.getFrom() == numTexton)
                .mapToInt(TextonLien::getTo).mapToObj(this::getTextonHeader).collect(Collectors.toList());
    }

}
