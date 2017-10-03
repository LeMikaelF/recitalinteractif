package textonclasses;

import javafx.scene.image.Image;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlJavaTypeAdapter(TextonAdapter.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class Texton {

    @XmlAttribute
    final private Integer numTexton;
    final private String source;
    final private String name;
    final private String description;
    final private String comment;
    @XmlJavaTypeAdapter(Base64Adapter.class)
    final private Image image;

    public Texton(Integer numTexton, String source, String name, String description, String comment, Image image) {

        this.numTexton = numTexton;
        this.source = source;
        this.name = name;
        this.description = description;
        this.comment = comment;
        this.image = image;
    }

    private Texton(){
        this(null, null, null, null, null, null);}

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

    @Override
    public String toString() {
        return "Texton{" +
                "numTexton=" + numTexton +
                ", source='" + source + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", image=" + image +
                '}';
    }
}
