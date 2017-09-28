package builder;

import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import textonclasses.Texton;
import textonclasses.TextonT;
import util.CanvasUtil;
import util.WritableTextonCanvas;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mikaël on 2017-01-08.
 */
public class VisContr {
    private WritableTextonCanvas wtc = new WritableTextonCanvas();

    public Stage getStage() {
        return stage;
    }

    private Stage stage = new Stage();

    VisContr(double width, double length) {
        CanvasUtil.setNodeAnchorToAnchorPane(wtc, 0, 0, 0, 0);
        AnchorPane anchorPane = new AnchorPane(wtc);
        stage.setScene(new Scene(anchorPane, width, length));
        stage.show();
        disableCloseRequest();
    }

    private void disableCloseRequest() {
        stage.setOnCloseRequest(Event::consume);
    }

    public List<Rectangle2D> getPercentRectangles() {
        return wtc.getPercentRectangles();
    }

    public void setTexton(Texton texton) {
        wtc.setTexton(texton);
    }

    public void setImage(File image) throws IOException {
        wtc.setTexton(new TextonT(999, "temp", "temp", image));
    }

    IntegerProperty numRectProperty() {
        return wtc.numRectProperty();
    }

    public void resetRectangles() {
        wtc.resetRectangles();
    }
}
