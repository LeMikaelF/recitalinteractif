package textonclasses.persistence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by Mikaël on 2017-10-07.
 */
public class ImageSerializer extends JsonSerializer<Image> {
    @Override
    public void serialize(Image image, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try (ByteArrayOutputStream bous = new ByteArrayOutputStream()) {
            byte[] bytes;
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", bous);
            //bytes = Base64.getMimeEncoder().encode(bous.toByteArray());
            //jsonGenerator.writeString(new String(bytes, "UTF-8"));
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(bous.toByteArray()));
        }
    }

    public ImageSerializer() {}
}
