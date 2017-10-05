package io;

import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonLien;

import java.io.IOException;
import java.util.List;

/**
 * Created by Mikaël on 2017-01-10.
 */
public abstract class TextonIo {
    protected String[] properties;
    TextonIo(String[] properties){
        this.properties = properties;
    };

    public abstract List<Texton> dumpTextons() throws IOException;
    public abstract List<TextonLien> dumpEdges() throws IOException;
    abstract public Texton readTexton(int numTexton) throws IOException;
    abstract public void writeTexton(Texton texton, boolean overwrite) throws IOException;
    abstract public Graph getGraph() throws IOException;
}
