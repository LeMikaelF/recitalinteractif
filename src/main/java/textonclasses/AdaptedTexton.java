/**
 * Created by Mikaël on 2017-10-02.
 */
package textonclasses;

import javafx.scene.image.Image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class AdaptedTexton {

    @XmlAttribute
    private Integer numTexton;
    private String source;
    private String name;
    private String description;
    private String comment;
    private Image image;

    public AdaptedTexton(Integer numTexton, String source, String name, String description, String comment, Image image) {

        this.numTexton = numTexton;
        this.source = source;
        this.name = name;
        this.description = description;
        this.comment = comment;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Integer getNumTexton() {
        return numTexton;
    }

    public void setNumTexton(Integer numTexton) {
        this.numTexton = numTexton;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
