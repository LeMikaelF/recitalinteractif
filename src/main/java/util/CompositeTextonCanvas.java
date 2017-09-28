package util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.layout.AnchorPane;
import textonclasses.Texton;

/**
 * Created by Mikaël on 2017-01-05.
 */
public class CompositeTextonCanvas extends AnchorPane implements TCWithTexton {
    private TextonImageCanvas textonImageCanvas = new TextonImageCanvas();
    //Pour l'affichage avec les rectangles:
    //    private TextonOverlayCanvas textonOverlayCanvas = new TextonOverlayCanvasWithRect();
    private TextonOverlayCanvas textonOverlayCanvas = new TextonOverlayCanvasOnlyText();


    {
        System.out.println("This is the right parent: " + this );
        getChildren().addAll(textonImageCanvas, textonOverlayCanvas);
        getChildren().forEach(node -> CanvasUtil.setNodeAnchorToAnchorPane(node, 0, 0, 0, 0));
        System.out.println("These are the right parent's children: " + getChildren());
    }

    @Override
    public Texton getTexton() {
        return textonImageCanvas.getTexton();
    }

    @Override
    public void setTexton(Texton texton) {

        textonImageCanvas.setTexton(texton);
        textonOverlayCanvas.setTexton(texton);
    }

    SimpleListProperty<Double> boundsProperty() {
        return textonImageCanvas.boundsProperty;
    }

    public BooleanProperty nullTextonProperty() {
        return textonImageCanvas.nullTextonProperty();
    }
}
