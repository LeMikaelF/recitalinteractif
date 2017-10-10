package textonclasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.scene.image.Image;
import textonclasses.persistence.ImageDeserializer;
import textonclasses.persistence.ImageSerializer;

public class Texton {

    private final TextonHeader textonHeader;
    private final String source;
    private final String description;
    private final String comment;
    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonSerialize(using = ImageSerializer.class)
    private final Image image;

    @JsonCreator
    public Texton(@JsonProperty("numTexton") Integer numTexton, @JsonProperty("source") String source,
                  @JsonProperty("name") String name, @JsonProperty("description") String description,
                  @JsonProperty("comment") String comment, @JsonProperty("image") Image image) {
        textonHeader = new TextonHeader(numTexton, name);
        this.source = source;
        this.description = description;
        this.comment = comment;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public Integer getNumTexton() {
        return textonHeader.getNumTexton();
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return textonHeader.getName();
    }

    public String getDescription() {
        return description;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Texton{" +
                "numTexton=" + getNumTexton() +
                ", source='" + source + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", image=" + image +
                '}';
    }

}
