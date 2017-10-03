package tests;

import io.TextonIo;
import io.XmlFileConnector;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.loadui.testfx.GuiTest;
import textonclasses.Texton;
import util.PropLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

/**
 * Created by Mikaël on 2017-10-02.
 */
public class XmlFileConnectorTest extends GuiTest{
    TextonIo textonIo;
    File imageFile;
    Texton texton;


    @Before
    public void before(){
        System.out.println("before test");
        imageFile = new File("C:\\Users\\Mika\u00EBl\\Desktop\\textons\\image.png");
        try {
            texton = new Texton(40, "ma source", "nom du texton", "Ceci est la description du texton",
                    "Ceci est un commentaire", new Image(imageFile.toURI().toURL().toExternalForm()));
            textonIo = new XmlFileConnector(new String[]{PropLoader.getMap().get("location")});
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Parent getRootNode() {
        return new AnchorPane();
    }


    public XmlFileConnectorTest() throws IOException, URISyntaxException {
    }

    @Test
    public void test1(){
        System.out.println(textonIo);
        try {
            textonIo.writeTexton(texton, false);
            Texton readTexton = textonIo.readTexton(40);
            System.out.println(texton);
            System.out.println(readTexton);
            Assert.assertThat(texton, sameBeanAs(readTexton).ignoring(Image.class).with("image", imageEquality(texton.getImage())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSamePropertyValuesAs(){
        Texton newTexton = new Texton(texton.getNumTexton(), texton.getSource(), texton.getName(),
                texton.getDescription(), texton.getComment(), texton.getImage());
        Assert.assertThat(texton, samePropertyValuesAs(newTexton));
    }

    static Matcher<Image> imageEquality(Image image1) {
        return new Matcher<Image>() {
            @Override
            public boolean matches(Object o) {
                if(!(o instanceof Image)) return false;
                Image image2 = (Image)o;
                if(image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) return false;

//TODO Ajouter la citation. Provient de: https://stackoverflow.com/questions/26044106/javafx-how-to-test-image-equality
                for (int i = 0; i < image1.getWidth(); i++)
                {
                    for (int j = 0; j < image1.getHeight(); j++)
                    {
                        if (image1.getPixelReader().getArgb(i, j) != image2.getPixelReader().getArgb(i, j)) return false;
                    }
                }

                return true;
            }

            @Override
            public void describeMismatch(Object o, Description description) {
                description.appendText("Les deux images ne sont pas identiques.");
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
