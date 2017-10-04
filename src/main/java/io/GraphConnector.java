package io;

import org.json.JSONArray;
import org.json.JSONObject;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonLien;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Mikaël on 2017-10-03.
 */
public class GraphConnector {

    public static void writeGraph(List<TextonLien> edges, List<Texton> nodes, Path path) throws IOException {
        Files.write(path, getJsonGraph(nodes, edges).getBytes());
    }

    public static Graph getGraph(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        String json = new String(bytes, StandardCharsets.UTF_8);
        return getGraphFromJson(json);
    }

    public static String getJsonGraph(List<Texton> nodes, List<TextonLien> edges) {
        JSONArray nodeArray = new JSONArray();
        for (Texton texton : nodes) {
        nodeArray.put(new JSONObject().put("id", texton.getNumTexton()).put("label", texton.getName()));
        }
        JSONArray edgeArray = new JSONArray();
        for (TextonLien lien : edges) {
            edgeArray.put(new JSONObject().put("from", lien.getFrom()).put("to", lien.getTo()));
        }
        return new JSONObject().put("nodes", nodeArray).put("edges", edgeArray).toString(4);
    }

    public static Graph getGraphFromJson(String jsonGraph){
        JSONObject jsonObject = new JSONObject(jsonGraph);

        JSONArray jsonNodes = jsonObject.getJSONArray("nodes");
        List<Integer> idNodes = new ArrayList<>();
        List<String> nameNodes = new ArrayList<>();
        Map<Integer, String> nodes = new HashMap<>();
        for (int i = 0; i < jsonNodes.length(); i++) {
            JSONObject jsonObjectInner = jsonNodes.getJSONObject(i);
            nodes.put(jsonObjectInner.getInt("id"), jsonObjectInner.getString("name"));
        }

        JSONArray jsonEdges = jsonObject.getJSONArray("edges");
        List<TextonLien> edges = new ArrayList<>();
        for (int i = 0; i < jsonEdges.length(); i++) {
            JSONObject jsonObjectInner = jsonEdges.getJSONObject(i);
            edges.add(new TextonLien(jsonObjectInner.getInt("from"), jsonObjectInner.getInt("to")));
        }

        return new Graph(nodes, edges);
    }
}
