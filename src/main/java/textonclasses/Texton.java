package textonclasses;

import util.MaxedArrayList;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by Mikaël on 2016-10-28.
 */
public abstract class Texton {

    BufferedImage bImage;
    private Integer numTexton;
    private String source;
    private String name;
    private TextonType type;
    private final List<LienTexton> lienTexton;
    private boolean isCompleted = false;
    private String description;

    Texton(int numTexton, String source, String name, TextonType type) {
        this.numTexton = numTexton;
        this.source = source;
        this.name = name;
        this.type = type;
        lienTexton = new MaxedArrayList<>(4, 4);
    }

    public Integer getNumTexton() {
        return numTexton;
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

    public TextonType getType() {
        return type;
    }

    public List<LienTexton> getLienTexton() {
        return lienTexton;
    }

    public BufferedImage getBimage() {
        return bImage;
    }

    void setBimage(BufferedImage bimage) {
        this.bImage = bimage;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumTexton(int numTexton) {
        this.numTexton = numTexton;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(TextonType type) {
        this.type = type;
    }
}
