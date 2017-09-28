package anim;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import server.ControlCode;

/**
 * Created by Mikaël on 2017-01-01.
 */
class TimerOverAnim extends BaseChangeAnim {
    final static private String title = "Temps écoulé!";
    final static private int loadTimeMillis = 4000;

    public TimerOverAnim(Pane pane, ControlCode lien, String numVotes) {
        super(pane, lien, title, loadTimeMillis);
        appendTextToLienXXLine(numVotes);
    }

    private void appendTextToLienXXLine(String str) {
        Text numVotesText = new Text();
        setTextStyle(numVotesText, "     (" + str + ")");
        appendToLienXXLine(numVotesText);
    }

}
