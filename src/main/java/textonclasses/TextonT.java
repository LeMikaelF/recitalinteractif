package textonclasses;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Mikaël on 2016-10-31.
 */
public class TextonT extends Texton implements TextonWithImage {

    TextonT(int numTexton, String source, String name, BufferedImage bImage) {
        super(numTexton, source, name, TextonType.TT);
        if (bImage == null) {
            throw new IllegalArgumentException("BufferedImage in texton constructor cannot be null.");
        }
        this.bImage = bImage;
    }

    public TextonT(int numTexton, String source, String name, File image) throws IOException {
        super(numTexton, source, name, TextonType.TT);
        bImage = ImageIO.read(image);
    }

}

