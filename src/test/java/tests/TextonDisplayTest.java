package tests;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.TextonIoFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Test;
import util.CanvasUtil;
import util.CompositeTextonCanvas;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Mikaël on 2017-10-11.
 */
public class TextonDisplayTest extends Application {

    Injector guice = Guice.createInjector(new GuiceTestModule());
    Path path = Paths.get("C:\\Users\\Mikaël\\Desktop\\textons");

    @Inject
    TextonIoFactory textonIoFactory;

    CompositeTextonCanvas canvas;

    @Test
    public void testTextonDisplay() throws IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        guice.injectMembers(this);
        canvas = new CompositeTextonCanvas();
        Stage stage = new Stage();
        Scene scene = new Scene(new AnchorPane(canvas), 800, 600);
        CanvasUtil.setNodeAnchorToAnchorPane(canvas, 0, 0, 0, 0);
        stage.setScene(scene);
        stage.show();

        canvas.setGraph(textonIoFactory.create(path).getGraph());
        canvas.setTexton(textonIoFactory.create(path).readTexton(1));
    }
}
