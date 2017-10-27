package events;

/**
 * Created by Mikaël on 2017-10-21.
 */
public class ObjectEvent {
    Object object;

    public ObjectEvent(Object object) {
        if(object == null) throw new IllegalArgumentException();
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}
