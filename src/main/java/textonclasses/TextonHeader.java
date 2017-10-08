package textonclasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mikaël on 2017-10-06.
 */

public class TextonHeader {
    final private int numTexton;
    final private String name;

    @JsonCreator
    public TextonHeader(@JsonProperty("id") int numTexton, @JsonProperty("label") String name) {
        this.numTexton = numTexton;
        this.name = name;
    }

    @JsonProperty("id")
    public int getNumTexton() {
        return numTexton;
    }

    @JsonProperty("label")
    public String getName() {
        return name;
    }
}
