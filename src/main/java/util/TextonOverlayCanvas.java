package util;

import textonclasses.Texton;

/**
 * Created by Mikaël on 2017-09-24.
 */
public abstract class TextonOverlayCanvas extends ResizableCanvas implements TCWithTexton {
    private Texton texton;

    @Override
    public abstract void draw();

    @Override
    public Texton getTexton() {
        return texton;
    }

    @Override
    public void setTexton(Texton texton) {
        this.texton = texton;
        draw();
        onSetTexton().run();
    }

    //Runs after draw() when new texton is set.
    protected Runnable onSetTexton() {
        return () -> {};
    }
}
