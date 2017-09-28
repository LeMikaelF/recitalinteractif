package textonclasses;

import javafx.geometry.Rectangle2D;

import java.io.Serializable;

/**
 * Created by Mikaël on 2016-11-03.
 */
public class LienTexton implements Serializable {
    private int numSerie;
    //Name variable is used for TextonM.
    private String name;
    private TextonType type;
    private Rectangle2D pos;

    public LienTexton(int numSerie, String name, TextonType type, Rectangle2D pos) {
        this.numSerie = numSerie;
        this.name = name;
        this.type = type;
        this.pos = pos;
    }

    public int getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(int numSerie) {
        this.numSerie = numSerie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TextonType getType() {
        return type;
    }

    public void setType(TextonType type) {
        this.type = type;
    }

    public Rectangle2D getPos() {
        return pos;
    }

    public void setPos(Rectangle2D pos) {
        this.pos = pos;
    }

    public boolean isValid(){
        return (pos.getMinX() >= 0 && pos.getMinY() >= 0 && pos.getMaxX() <= 100.001 && pos.getMaxY() <= 100.001 && numSerie > 0);
    }
}