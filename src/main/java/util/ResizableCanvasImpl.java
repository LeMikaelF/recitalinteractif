package util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Created by Mikaël on 2017-10-04.
 */
public class ResizableCanvasImpl extends ResizableCanvas {
    private Image image;

    public void setImage(Image image) {
        this.image = image;
        draw();
    }

    @Override
    protected void draw() {
        clearCanvas();
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        if (image != null)
            fitImage(SwingFXUtils.fromFXImage(image, null));
        System.out.println("This is ResizableCanvasImpl drawing");
    }

}
