package util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import textonclasses.Texton;

/**
 * Created by Mikaël on 2016-12-04.
 */
//Cette classe ne fait qu'afficher l'image du texton.
class TextonImageCanvas extends ResizableCanvas implements TCWithTexton {
    private Texton texton;
    BooleanProperty nullTextonProperty = new SimpleBooleanProperty(true);

    @Override
    public void draw() {
        clearCanvas();
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        if (texton != null)
            fitImage(texton.getBimage());
    }

    @Override
    public Texton getTexton() {
        return texton;
    }

    @Override
    public void setTexton(Texton texton) {
        this.texton = texton;
        nullTextonProperty.set(texton == null);
        draw();
    }

    public BooleanProperty nullTextonProperty() {
        return nullTextonProperty;
    }
}
