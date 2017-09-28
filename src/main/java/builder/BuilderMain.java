package builder;/**
 * Created by Mikaël on 2017-01-07.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BuilderMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/BuilderContr.fxml"));
        root = loader.load();

        primaryStage.setTitle("Outil de lecture/écriture de textons");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
