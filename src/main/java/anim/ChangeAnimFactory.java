package anim;

import javafx.scene.layout.Pane;
import server.ControlCode;

/**
 * Created by Mikaël on 2016-12-30.
 */
public class ChangeAnimFactory {
    public static ChangeAnim createCriticalChangeAnim(Pane pane, ControlCode lien) {
        return new CritChangeAnim(pane, lien);
    }

    public static ChangeAnim createTimerOverAnim(Pane pane, ControlCode lien, String numVotes) {
        return new TimerOverAnim(pane, lien, numVotes);
    }
}

