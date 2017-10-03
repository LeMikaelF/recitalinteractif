package textonclasses;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Mikaël on 2017-10-03.
 */
public class Base64Adapter extends XmlAdapter<byte[], Image> {

    @Override
    public Image unmarshal(byte[] v) throws Exception {
        /*InputStream is = new ByteArrayInputStream(v);
        BufferedImage bufferedImage = ImageIO.read(is);
        Image writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        is.close();*/
        InputStream is = new ByteArrayInputStream(v);
        Image image = new Image(is);
        //new Image(is);
        return image;

    }

    @Override
    public byte[] marshal(Image v) throws Exception {
        System.out.println("Dimensaions de l'image: "  + v.getWidth() + ", " + v.getHeight());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(v, null), "png", os);
        os.flush();
        byte[] result = os.toByteArray();
        os.close();
        return result;
    }
}
