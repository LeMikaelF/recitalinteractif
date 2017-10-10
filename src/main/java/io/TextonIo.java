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
    public abstract Texton readTexton(int numTexton) throws IOException;
    public abstract void writeTexton(Texton texton, boolean overwrite) throws IOException;
    public abstract Graph getGraph() throws IOException;
    public abstract TextonHeader readTextonHeader(int numTexton) throws IOException;
    public abstract void saveGraph(Graph graph) throws IOException;
    public abstract boolean validateGraph() throws IOException;
}
