package sql;

import textonclasses.TextonType;
import util.Util;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;

/**
 * Created by Mikaël on 2017-09-15.
 */
public class SqlJsonSerializer {
    public static String serializeGraph(List<Util.Triple<Integer, String, TextonType>> nodes, List<List<Integer>> edges) {
    /*Returns a serialized Json object of format:
        {
            nodes:
            [
                {
                    num: 0
                    name: "text"
                    type: "TM"
                }
            ]
            edges:
            [
            {
                source: 0
                dest: 0
            }
            ]
        }*/

        JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
        JsonObjectBuilder linkBuilder = Json.createObjectBuilder();
        JsonArrayBuilder linkArrayBuilder = Json.createArrayBuilder();
        for (Util.Triple<Integer, String, TextonType> node : nodes) {
            JsonObjectBuilder tempBuilder = Json.createObjectBuilder();
            tempBuilder.add("id", "n" + node.getA());
            tempBuilder.add("label", node.getB());
            tempBuilder.add("x", 0);
            tempBuilder.add("y", 0);
            tempBuilder.add("size", 3);
            //tempBuilder.add("type", node.getC().toString());
            linkBuilder.addAll(tempBuilder);
            linkArrayBuilder.add(linkBuilder);
        }
        resultBuilder.add("nodes", linkArrayBuilder);

        JsonObjectBuilder edgeBuilder = Json.createObjectBuilder();
        JsonArrayBuilder edgeArrayBuilder = Json.createArrayBuilder();
        int i = 0;
        for (List<Integer> edge : edges) {
            JsonObjectBuilder tempBuilder = Json.createObjectBuilder();
            tempBuilder.add("id", "e" + i++);
            tempBuilder.add("source", "n" + edge.get(0));
            tempBuilder.add("target", "n" + edge.get(1));
            edgeBuilder.addAll(tempBuilder);
            edgeArrayBuilder.add(edgeBuilder);
        }
        resultBuilder.add("edges", edgeArrayBuilder);

        String result = resultBuilder.build().toString();
        return result;
    }
}
