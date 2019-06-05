package events;

/**
 * Created by Mikaël on 2017-10-21.
 */
public class ControlObjectEvent extends ObjectEvent {

    private ControlEvent controlEvent;

    public ControlEvent getControlEvent() {
        return controlEvent;
    }

    public ControlObjectEvent(Object object, ControlEvent controlEvent) {
        super(object);
        if(controlEvent == null) throw new IllegalArgumentException();
        this.controlEvent = controlEvent;
    }
}
