package io;

import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonHeader;
import textonclasses.TextonLien;

import java.io.IOException;
import java.util.List;

/**
 * Created by Mikaël on 2017-01-10.
 */
public abstract class TextonIo {
    public abstract List<Texton> dumpTextons() throws IOException;
    public abstract List<TextonHeader> dumpTextonHeaders() throws IOException;
    public abstract List<TextonLien> dumpEdges() throws IOException;
    abstract public Texton readTexton(int numTexton) throws IOException;
    abstract public void writeTexton(Texton texton, boolean overwrite) throws IOException;
    abstract public Graph getGraph() throws IOException;
    abstract public TextonHeader readTextonHeader(int numTexton) throws IOException;
    abstract public void saveGraph(Graph graph) throws IOException;
    abstract public boolean validateGraph() throws IOException;
}
