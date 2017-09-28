package textonclasses;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by Mikaël on 2016-10-31.
 */
public class TextonM extends Texton {
    private static final String defaultTextonMImage = "./resources/TMdefault.png";

    TextonM(int numTexton, String source, String name) {
        super(numTexton, source, name, TextonType.TM);
        try {
            setBimage(ImageIO.read(new File(defaultTextonMImage)));
        } catch (IOException e) {
            System.out.println("Could not instanciate new TextonM.");
            e.printStackTrace();
        }
    }

}
