package anim;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;

/**
 * Created by Mikaël on 2016-12-30.
 */
public interface ChangeAnim {
    Node getNode();
    void play();
    BooleanProperty isOverProperty();
}
