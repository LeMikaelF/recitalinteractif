package util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Created by Mikaël on 2017-01-05.
 */
class PercentCoordProperty extends SimpleObjectProperty<Point2D> {
    private static final Rectangle2D BOUNDS = new Rectangle2D(0, 0, 100, 100);
    @Override
    public void set(Point2D newValue) {
        if (BOUNDS.contains(newValue)) set(newValue);
        else throw new IllegalArgumentException("Specified coordinates are illegal.");
    }

    public void setWithCoordsAndBounds(Point2D coord, Rectangle2D fullBounds) {
        double x = coord.getX() * 100 / fullBounds.getWidth();
        double y = coord.getY() * 100 / fullBounds.getHeight();
        set(new Point2D(x, y));
    }
}
