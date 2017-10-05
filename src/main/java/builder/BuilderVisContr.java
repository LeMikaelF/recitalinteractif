/**
 * Created by Mikaël on 2017-10-04.
 */
package builder;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import util.Util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderVisContr {

    @FXML
    private MenuItem menuOuvrir;

    @FXML
    private MenuItem menuRecharger;

    @FXML
    private MenuItem menuEnregistrer;

    @FXML
    private MenuItem menuEnregistrerSous;

    @FXML
    private MenuItem menuAjuster;

    @FXML
    private MenuItem menuFermer;

    @FXML
    private WebView webView;

    @Inject
    private EventBus eventBus;

    private Path path;
    private Stage stage;
    List<Stage> stageList = Stream.generate(Stage::new).limit(1).collect(Collectors.toList());

    public Stage getStage() {
        return stageList.get(0);
    }

    @FXML
    void menuHandler(ActionEvent event) {
        if (event.getSource().equals(menuOuvrir)) {

        } else if (event.getSource().equals(menuRecharger)) {

        } else if (event.getSource().equals(menuEnregistrer)) {

        } else if (event.getSource().equals(menuEnregistrerSous)) {

        } else if (event.getSource().equals(menuAjuster)) {

        } else if (event.getSource().equals(menuFermer)) {

        }
    }

    @FXML
    void initialize() {
        eventBus.register(this);
        Util.initializeStageRetriever(webView, stageList);
    }
}
