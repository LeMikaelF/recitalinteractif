package util;

import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import textonclasses.TextonWithImage;

/**
 * Created by Mikaël on 2016-12-04.
 */
//Cette classe ne fait que dessiner ce qui va par-dessus l'image du texton.
//Cette version
public class TextonOverlayCanvasWithRect extends TextonOverlayCanvas {

    @Override
    public void draw() {
        clearCanvas();
        if (getTexton() != null) {
            if (getTexton() instanceof TextonWithImage) drawOverlayWithImage();
            else drawOverlayNoImage();
        }
    }

    private void drawOverlayWithImage() {
        getGraphicsContext2D().setEffect(new Bloom(0.1));
        getTexton().getLienTexton().forEach(lienTexton -> CanvasUtil.drawLinkOnCanvas(lienTexton.getPos(),
                getGraphicsContext2D(), CanvasUtil.getLinkToColorMap().get(getTexton().getLienTexton().indexOf(lienTexton)),
                getFittedCoords(getTexton().getBimage())));
    }

    private void drawOverlayNoImage() {
        CanvasUtil.drawLinksAndNumOnTMCanvas(getTexton().getLienTexton(), getGraphicsContext2D(), Color.CHOCOLATE);
    }

}
