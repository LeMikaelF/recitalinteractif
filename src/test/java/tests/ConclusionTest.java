package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import textonclasses.Graph;
import textonclasses.TextonHeader;
import util.FXCustomDialogs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-10-11.
 */
public class ConclusionTest extends Application {
    List<TextonHeader> list = Stream.of(
            new TextonHeader(1, "Node 1"),
            new TextonHeader(3, "Node 3"),
            new TextonHeader(4, "Node 4")
    ).collect(Collectors.toList());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        WebView webView = new WebView();
        Group group = new Group();
        group.getChildren().add(webView);
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.show();

        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/public/conclusion/conclusion.html").toExternalForm());
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Worker.State.SUCCEEDED)) {
                try {
                    //This is required to properly serialze a List<TextonHeader>
                    ObjectMapper mapper = new ObjectMapper();
                    TypeFactory typeFactory = mapper.getTypeFactory();
                    JavaType javaType = typeFactory.constructParametricType(ArrayList.class, TextonHeader.class);



                    URL resource = getClass().getResource("/graph.json");
                    Path path = Paths.get(resource.toURI());
                    webEngine.executeScript("receiveJson('" + Arrays.toString(Files.readAllBytes(path)) + "')");
                    webEngine.executeScript("receiveTextonPath('" +
                            new ObjectMapper().writer().forType(javaType).writeValueAsString(list) + "')");
                } catch (JsonProcessingException e) {
                    FXCustomDialogs.showError("Impossible d'initialiser la conclusion. Veuillez réactualiser la page.");
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
