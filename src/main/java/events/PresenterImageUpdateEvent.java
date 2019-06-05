package events;

import javafx.scene.image.Image;

/**
 * Created by Mikaël on 2017-10-04.
 */
public class PresenterImageUpdateEvent {
    private final Image image;

    public PresenterImageUpdateEvent(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }
}
