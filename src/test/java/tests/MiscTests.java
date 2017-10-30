package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.junit.Test;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.*;
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
        assertThat(textonHeader, samePropertyValuesAs(newTextonHeader));
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
        assertThat(texton, sameBeanAs(newTexton).ignoring(Image.class).with("image", imageEquality(texton.getImage())));
    }

    @Test
    public void testGraphSerializationAndDeserialization() throws IOException {
        Graph graph = new ObjectMapper().readValue(getClass().getResourceAsStream("/graph.json"), Graph.class);
        String temp = new ObjectMapper().writeValueAsString(graph);
        Graph newGraph = new ObjectMapper().readValue(temp, Graph.class);
        String graphString = graph.getNodes().stream().map(Object::toString).reduce((s, s2) -> s + s2).toString();
        String newGraphString = graph.getNodes().stream().map(Object::toString).reduce((s, s2) -> s + s2).toString();

        System.out.println(graphString);
        System.out.println(newGraphString);
        assertEquals(graphString, newGraphString);
    }

    @Test
    public void testGraphSerializationRemoveTexton0() throws IOException {
        //This should contain Texton 0
        Graph graph = new ObjectMapper().readValue(getClass().getResourceAsStream("/graph.json"), Graph.class);
        String graphString = graph.getNodes().stream().map(Object::toString).reduce((s, s2) -> s + s2).get();
        //This should not contain Texton 0.
        String serialized = new ObjectMapper().writeValueAsString(graph);
        System.out.println(serialized);
        assertFalse(serialized.contains("Texton 0"));
        assertTrue(graphString.contains("Texton 0"));
    }
}