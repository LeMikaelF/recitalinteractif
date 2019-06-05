package tests;

import javafx.scene.image.Image;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by Mikaël on 2017-10-07.
 */

//TODO Extend BaseMatcher, see second last overridden method.
class Util {
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
