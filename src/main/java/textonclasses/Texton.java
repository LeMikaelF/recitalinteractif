package textonclasses;

import javafx.scene.image.Image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Texton {

    @XmlAttribute
    private Integer numTexton;
    private String source;
    private String name;
    private String description;
    private String comment;
    private Image image;

    private Texton(Integer numTexton, String source, String name, String description, String comment, Image image) {

        this.numTexton = numTexton;
        this.source = source;
        this.name = name;
        this.description = description;
        this.comment = comment;
        this.image = image;
    }

    public static Texton createTexton(Integer numTexton, String source, String name, String description, String comment, Image image) {
        return new Texton(numTexton, source, name, description, comment, image);
    }

    public Image getImage() {
        return image;
    }

    public Integer getNumTexton() {
        return numTexton;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getComment() {
        return comment;
    }
}
