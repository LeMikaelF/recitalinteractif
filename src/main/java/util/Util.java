package util;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import server.Vote;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Mikaël on 2016-10-31.
 */
public class Util {

    public static Image getScaledView(Node node, int scale) {
        //TODO Compléter la citation
        //Cette méthode est adaptée de http://news.kynosarges.org/2017/02/01/javafx-snapshot-scaling/
        final Bounds bounds = node.getLayoutBounds();

        final WritableImage image = new WritableImage(
                (int) Math.round(bounds.getWidth() * scale),
                (int) Math.round(bounds.getHeight() * scale));

        final SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(javafx.scene.transform.Transform.scale(scale, scale));

        return node.snapshot(spa, image);
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        /*Cette méthode est tirée de Bohemian (nom d'utilisateur). 2015. Réponse à « Getting all names in an enum as
        String[] ». Stack Overflow. 11 septembre. Consulté le 2 janvier 2017.
        <http://stackoverflow.com/questions/13783295/getting-all-names-in-an-enum-as-a-string>*/
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public static String getFormattedNumSerie(int numSerie) {
        String pattern = "000";
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA_FRENCH);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(numSerie);
    }

    public static <T extends Comparable<T>> T clamp(T min, T max, T val) {
        if (val.compareTo(min) < 0) val = min;
        else if (val.compareTo(max) > 0) val = max;
        return val;
    }

    //Cette méthode provient de Klemming, Jonas et Manos Nikolaidis. 2016. Réponse à « What's the best way to see
    //if a string represents an integer in Java? ». Stack Overflow. 7 mars. Consulté le 26 décembre 2016.
    // <http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java>.
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static int ccToInt(Vote cc) {
        Map<Vote, Integer> map = new HashMap<>();
        map.put(Vote.A, 0);
        map.put(Vote.B, 1);
        map.put(Vote.C, 2);
        map.put(Vote.D, 3);
        return map.get(cc);
    }

    public static void toggleProperty(BooleanProperty prop) {
        prop.set(!prop.get());
    }
}
