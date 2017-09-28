package textonclasses;

import java.awt.image.BufferedImage;

/**
 * Created by Mikaël on 2016-10-31.
 */
public class TextonV extends Texton implements TextonWithImage {
    public static final int MINIMUMTIMER = 20;
    private final int timer;

    TextonV(int numTexton, String source, String name, int timer, BufferedImage bImage) {
        super(numTexton, source, name, TextonType.TV);
        this.timer = timer;
        this.bImage = bImage;
    }

    public int getTimer() {
        return timer;
    }

}
