package textonclasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TextonLien implements Serializable {
    private final int from;
    private final int to;

    @JsonCreator
    public TextonLien(@JsonProperty("from") int from, @JsonProperty("to") int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}