package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.junit.Assert;
import org.junit.Test;
import textonclasses.Texton;
import textonclasses.TextonHeader;

import javax.imageio.ImageIO;
import javax.sound.midi.Receiver;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static tests.Util.imageEquality;

/**
 * Created by Mikaël on 2017-10-03.
 */
public class MiscTests {

    @Test
    public void testTextonHeader() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TextonHeader textonHeader = new TextonHeader(15, "nom du texton");
        String serialized = objectMapper.writeValueAsString(textonHeader);
        TextonHeader newTextonHeader = objectMapper.readValue(serialized, TextonHeader.class);
        Assert.assertThat(textonHeader, samePropertyValuesAs(newTextonHeader));
        System.out.println(serialized);
    }

    @Test
    public void testTexton() throws IOException {
        BufferedImage read = ImageIO.read(getClass().getResourceAsStream("/image.png"));
        Image image = SwingFXUtils.toFXImage(read, null);
        Texton texton = new Texton(14, "source", "nom", "description", "commentaire", image);

        ObjectMapper objectMapper = new ObjectMapper();
        String serialized = objectMapper.writeValueAsString(texton);
        System.out.println(serialized);
        Texton newTexton = objectMapper.readValue(serialized, Texton.class);
        Assert.assertThat(texton, sameBeanAs(newTexton).ignoring(Image.class).with("image", imageEquality(texton.getImage())));
    }


}
