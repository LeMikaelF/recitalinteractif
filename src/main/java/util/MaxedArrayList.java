package util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mikaël on 2016-11-01.
 */
//TODO Pousser cette classe vers un autre repository pour la garder.
public class MaxedArrayList<E> extends ArrayList<E> {
    private final int MAXSIZE;

    public MaxedArrayList(int initialCapacity, final int MAXSIZE) {
        super(initialCapacity);
        this.MAXSIZE = MAXSIZE;
    }

    public MaxedArrayList(final int MAXSIZE) {
        super();
        this.MAXSIZE = MAXSIZE;
    }

    public MaxedArrayList(Collection<? extends E> c, final int MAXSIZE) {
        super(c);
        this.MAXSIZE = MAXSIZE;
        sizeCheck();
    }

    private boolean sizeCheck() {
        //returns true if operation is legal.
        return (size() <= MAXSIZE);
    }

    private boolean sizeCheck(int deltaElements) {
        if (deltaElements < 0) throw new IllegalArgumentException();
        //returns true if operation is legal.
        return (size() + deltaElements <= MAXSIZE);
    }

    @Override
    public void add(int index, E element) throws IllegalStateException {
        if (!sizeCheck()) throw throwException();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) throws IllegalStateException {
        if (!sizeCheck(c.size())) throw throwException();
        return (super.addAll(c));
    }

    private IllegalStateException throwException() {
        return new IllegalStateException("Request is over MaxArrayList max size. Elements not added.");
    }
}
