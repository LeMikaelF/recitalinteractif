package util;

/**
 * Created by Mikaël on 2017-01-06.
 */
public class Wrapper<T> {
    //This class is used to treat an immutable object as mutable.
    T t;

    public void setT(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }
}
