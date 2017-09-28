package textonclasses;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Mikaël on 2016-12-29.
 */
public class TextonFactory {

    final private static String ABORTMSG = "Texton construction aborted.";
    final private static String NUMTEXTONMSG = "Texton Number must be > 0." + ABORTMSG;
    final private static String BIMAGEMSG = "BufferedImage cannot be null. " + ABORTMSG;
    final private static String TIMERMSG = "Timer must be > 5 seconds. " + ABORTMSG;

    public static TextonT createTextonT(int numTexton, String source, String name, BufferedImage bImage) throws IOException {
        numTextonCheck(numTexton);
        bImageCheck(bImage);
        return new TextonT(numTexton, source, name, bImage);
    }

    public static TextonV createTextonV(int numTexton, String source, String name, int timer, BufferedImage bImage) throws IOException {
        numTextonCheck(numTexton);
        bImageCheck(bImage);
        timerCheck(timer);
        return new TextonV(numTexton, source, name, timer, bImage);
    }

    public static TextonM createTextonM(int numTexton, String source, String name) throws IOException {
        numTextonCheck(numTexton);
        return new TextonM(numTexton, source, name);
    }

    private static void numTextonCheck(int numTexton) {
        if (numTexton <= 0) throw new IllegalArgumentException(NUMTEXTONMSG);
    }

    private static void bImageCheck(BufferedImage bImage) {
        if (bImage == null)
            throw new IllegalArgumentException(BIMAGEMSG);
    }

    private static void timerCheck(int timer) {
        if (timer <= 5) throw new IllegalArgumentException(TIMERMSG);
    }
}
