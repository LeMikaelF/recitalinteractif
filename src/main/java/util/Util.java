package util;

import javafx.beans.property.BooleanProperty;
import server.ControlCode;

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

    public static int ccToInt(ControlCode cc) {
        Map<ControlCode, Integer> map = new HashMap<>();
        map.put(ControlCode.A, 0);
        map.put(ControlCode.B, 1);
        map.put(ControlCode.C, 2);
        map.put(ControlCode.D, 3);
        return map.get(cc);
    }

    public static void blipProperty(BooleanProperty prop, boolean blipTo) {
        prop.set(blipTo);
        prop.set(!blipTo);
    }

    public static void blipProperty(BooleanProperty prop) {
        prop.set(!prop.get());
        prop.set(!prop.get());
    }

    public static void toggleProperty(BooleanProperty prop) {
        prop.set(!prop.get());
    }


    public static class Triple<A, B, C>{
        private A a;
        private B b;
        private C c;

        public Triple(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }

        public C getC() {
            return c;
        }
    }
}
