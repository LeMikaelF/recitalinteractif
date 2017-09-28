package util;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Mikaël on 2017-01-05.
 */
public class WritableTextonCanvas extends CompositeTextonCanvas {
    private static final int INITNUMRECT = 4;
    IntegerProperty numRect = new SimpleIntegerProperty(INITNUMRECT);
    IntegerProperty transitory = new SimpleIntegerProperty(INITNUMRECT);
    List<ResizableRectangle> rectList = new ArrayList<>(numRect.get());
    ChangeListener<Number> numRectListener = (observable, oldValue, newValue) -> {
        getChildren().removeAll(rectList);
        //TODO peut causer des problèmes à cause des effets secondaires (si implémentation parallèle, remplacer par map(). … add()
        rectList.stream().limit(newValue.intValue()).forEach(resizableRectangle -> getChildren().add(resizableRectangle));
    };

    {
        //Initialization block
        numRect.addListener(numRectListener);
        IntStream.range(0, numRect.get()).forEach(i -> rectList.add(new ResizableRectangle(new Rectangle2D(100 + (10 * i), 100 + (10 * i), 100, 100),
                CanvasUtil.getLinkToColorMap().get(i))));
        numRect.bind(Bindings.when(nullTextonProperty()).then(0).otherwise(transitory));
        System.out.println(nullTextonProperty());
        rectList.forEach(resizableRectangle -> resizableRectangle.fitInBounds(boundsProperty()));
    }

    public void resetRectangles(){
        IntStream.range(0, rectList.size()).forEach(i -> rectList.get(i).moveRectangle(new Rectangle2D(100 + (10 * i), 100 + (10 * i), 100, 100)));
    }

    public IntegerProperty numLinkProperty() {
        return numRectProperty();
    }

    public List<Rectangle2D> getPercentRectangles() {
        System.out.println("Liste des rectangles avant transformation:-----------------------------------------------" + rectList);
        return rectList.stream().limit(numRect.get()).map(resizableRectangle ->
                CanvasUtil.realCoordsToPercentRect(resizableRectangle.asRectangle2D(), new Rectangle2D(boundsProperty().get(0),
                        boundsProperty().get(1), boundsProperty().get(2) - boundsProperty().get(0),
                        boundsProperty().get(3) - boundsProperty().get(1))))
                .collect(Collectors.toList());
    }

    public IntegerProperty numRectProperty() {
        //Property is exposed via transitory property that guarantees that no rectangle is shown when texton is null.
        return transitory;
    }

}
