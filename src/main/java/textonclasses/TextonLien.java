package textonclasses;

import java.io.Serializable;


public class TextonLien implements Serializable {
private int from;
private int to;

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public TextonLien(int from, int to) {

        this.from = from;
        this.to = to;
    }
}