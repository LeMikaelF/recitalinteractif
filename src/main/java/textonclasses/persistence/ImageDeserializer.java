package textonclasses.persistence;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by Mikaël on 2017-10-07.
 */
public class ImageDeserializer extends JsonDeserializer<Image> {

    @Override
    public Image deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String encoded = node.toString();
        byte[] bytes = Base64.getMimeDecoder().decode(encoded);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            BufferedImage bufferedImage = ImageIO.read(bais);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            return image;
        }
    }

    public ImageDeserializer() {}
}
