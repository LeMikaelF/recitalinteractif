package anim;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;

/**
 * Created by Mikaël on 2016-12-30.
 */
public interface CounterAnim {
    Node getNode();
    void play();
    BooleanProperty isOverProperty();
    double getSize();
    IntegerProperty counterProperty();
}
