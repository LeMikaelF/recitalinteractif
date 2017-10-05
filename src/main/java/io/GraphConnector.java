package io;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonLien;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by Mikaël on 2017-10-03.
 */
public class GraphConnector {

    public static void writeGraph(List<TextonLien> edges, List<Texton> nodes, Path path) throws IOException {
        Files.write(path, getJsonGraph(nodes, edges).getBytes());
    }

    public static Graph getGraph(Path path) throws IOException {
        //If file is empty
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path.resolve("graph.json").toFile()));
        if (bufferedReader.readLine() == null) {
            //File is empty, build empty graph json.
            bufferedReader.close();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.resolve("graph.json").toFile()));
            bufferedWriter.write("{nodes:{}, edges{}}");
            bufferedWriter.close();
        }

        String json = getJsonGraph(path);
        return getGraphFromJson(json);
    }

    public static String getJsonGraph(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path.resolve("graph.json"));
        String json = new String(bytes, StandardCharsets.UTF_8);
        return json;
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

    public static Graph getGraphFromJson(String jsonGraph) {
        JSONObject jsonObject = new JSONObject(jsonGraph);

        JSONArray jsonNodes = jsonObject.getJSONArray("nodes");
        List<Integer> idNodes = new ArrayList<>();
        List<String> nameNodes = new ArrayList<>();
        Map<Integer, String> nodes = new HashMap<>();
        for (int i = 0; i < jsonNodes.length(); i++) {
            JSONObject jsonObjectInner = jsonNodes.getJSONObject(i);
            nodes.put(jsonObjectInner.getInt("id"), jsonObjectInner.getString("label"));
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
