package anim;

/**
 * Created by Mikaël on 2016-12-30.
 */
public class CounterFactory {
    public static CinemaCounter createCinemaCounter(int counterInit, double size) {
        return new CinemaCounter(counterInit, size);
    }
}
