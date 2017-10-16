package textonclasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mikaël on 2017-09-28.
 */

public class Graph {

    private final List<TextonHeader> nodes;
    private final List<TextonLien> edges;
    private TextonHeader texton0 = new TextonHeader(0, "Texton 0");

    @JsonCreator
    public Graph(@JsonProperty("nodes") List<TextonHeader> nodes, @JsonProperty("edges") List<TextonLien> edges) {
        this.nodes = nodes;
        //This is necessary in order to textons from 1.
        //But it is not serialized to Json (see private method annotated @JsonProperty).
        nodes.add(0, texton0);
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
        return nodes.stream().filter(textonHeader -> textonHeader.getNumTexton() == numTexton).findFirst().orElse(null);
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

    @JsonProperty("nodes")
    private List<TextonHeader> getSimplifiedTextonHeaders(){
        ArrayList<TextonHeader> temp = new ArrayList<>(nodes);
        temp.remove(texton0);
        return temp;
    }

}
