package presentation;

import javafx.beans.property.ReadOnlyIntegerProperty;
import textonclasses.TextonHeader;

import java.util.List;
import java.util.Map;

/**
 * Created by Mikaël on 2017-09-29.
 */
public interface CommsManager {
    List<TextonHeader> getTextonPath();
}
