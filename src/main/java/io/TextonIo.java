package io;

import textonclasses.Texton;

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
    abstract public Texton readTexton(int numTexton) throws IOException;
    abstract public void writeTexton(Texton texton, boolean overwrite) throws IOException;
    abstract public String getTextonName(int numTexton) throws IOException;
    abstract public String getGraphXml() throws IOException;
}
