package anim;

import javafx.scene.layout.Pane;
import server.ControlCode;

/**
 * Created by Mikaël on 2017-01-01.
 */
class CritChangeAnim extends BaseChangeAnim {
    final static private String title = "Seuil critique activé.";
    final static private int loadTimeMillis = 8000;

    CritChangeAnim(Pane pane, ControlCode lien) {
        super(pane, lien, title, loadTimeMillis);
    }
}
