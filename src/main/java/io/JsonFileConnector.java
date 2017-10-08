package io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonHeader;
import util.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikaël on 2017-10-07.
 */
public class JsonFileConnector extends TextonFileIo {

    @Inject
    public JsonFileConnector(@Assisted Path path) throws IOException {
        super(path);


        //If file does not exist or is empty
        //Désactivé parce que la vérification du fichier ne fonctionne pas, toujours positif (toujours == 0)
        //if(path.resolve("graph.json").toFile().length() == 0) initGraph();

    }

    private void initGraph() throws IOException{
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(getPath().resolve("graph.json").toFile(), new Graph(new ArrayList<>(), new ArrayList<>()));
    }

    @Override
    public void saveGraph(Graph graph) throws IOException {
        try (FileWriter fileWriter = new FileWriter(getPath().resolve("graph.json").toFile())) {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(getPath().resolve("graph.json").toFile(), graph);
        }
    }

    @Override
    public Texton readTexton(int numTexton) throws IOException {
        String formattedNum = Util.getFormattedNumSerie(numTexton);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getPath().resolve(getPath().resolve(formattedNum + ".json")).toFile(), Texton.class);
    }

    @Override
    public void writeTexton(Texton texton, boolean overwrite) throws IOException {
        String formattedNum = Util.getFormattedNumSerie(texton.getNumTexton());
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(path.resolve(formattedNum + ".json").toFile(), texton);
    }

    @Override
    public Graph getGraph() throws IOException {
        return new ObjectMapper().readValue(getPath().resolve("graph.json").toFile(), Graph.class);
    }

    @Override
    public TextonHeader readTextonHeader(int numTexton) throws IOException {
        Texton texton = readTexton(numTexton);
        return new TextonHeader(texton.getNumTexton(), texton.getName());
    }

    @Override
    public boolean validateGraph() throws IOException {
        List<TextonHeader> textonHeaders = dumpTextonHeaders();
        Graph newGraph = new Graph(textonHeaders, getGraph().getEdges());
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(path.resolve("graph.json").toFile(), newGraph);
        return true;
    }
}
