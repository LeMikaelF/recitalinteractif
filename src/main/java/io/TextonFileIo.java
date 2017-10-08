package io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonHeader;
import textonclasses.TextonLien;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Mikaël on 2017-10-07.
 */
public abstract class TextonFileIo extends TextonIo {

    final Path path;

    TextonFileIo(Path path) throws JsonProcessingException {
        if (Files.isDirectory(path))
            this.path = path;
        else
            this.path = path.getParent();
    }

    @Override
    public List<Texton> dumpTextons() throws IOException {
        ConcurrentLinkedQueue<Texton> queue = new ConcurrentLinkedQueue<>();
        Files.newDirectoryStream(path, "[0-9][0-9][0-9].json").forEach(path -> {
            try {
                queue.add(readTexton(Integer.parseInt(path.getFileName().toString().substring(0, 3))));
            } catch (IOException e) {
                System.err.println("Error walking directory while dumping textons.");
                e.printStackTrace();
            }
        });
        Texton[] textonArray = queue.toArray(new Texton[0]);

        return Arrays.asList(textonArray);
    }

    @Override
    public List<TextonHeader> dumpTextonHeaders() throws IOException {
        ConcurrentLinkedQueue<TextonHeader> queue = new ConcurrentLinkedQueue<>();
        Files.newDirectoryStream(path, "[0-9][0-9][0-9].json").forEach(path -> {
            try {
                System.out.println("lecture du texton " + path.toString());
                TextonHeader textonHeader = readTextonHeader(Integer.parseInt(path.getFileName().toString().substring(0, 3)));
                System.out.println("Ceci a été récupéré: " + textonHeader + ", avec les informations: " + textonHeader.getNumTexton() + ", " + textonHeader.getName());
                queue.add(textonHeader);
            } catch (IOException e) {
                System.err.println("Error walking directory while dumping texton headers.");
                e.printStackTrace();
            }
        });
        TextonHeader[] textonHeaders = queue.toArray(new TextonHeader[0]);
        return Arrays.asList(textonHeaders);
    }

    @Override
    public List<TextonLien> dumpEdges() throws IOException {
        return getGraph().getEdges();
    }



    final protected Path getPath() {
        return path;
    }

}
