package presentation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.ServletGuiceModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import guice.FxGuiceModule;

import java.io.IOException;

public class Main extends Application {
    Injector guice = Guice.createInjector(new FxGuiceModule(), new ServletGuiceModule());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root;
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> guice.getInstance(TabBordContr.class));
        loader.setLocation(getClass().getResource("/fxml/TableauBord.fxml"));
        root = loader.load();

        primaryStage.setTitle("Tableau de bord de récital");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        Parent root2;
        FXMLLoader loader2 = new FXMLLoader();
        loader2.setControllerFactory(param -> guice.getInstance(VisContr.class));
        loader2.setLocation(getClass().getResource("/fxml/Visualisation.fxml"));
        root2 = loader2.load();

        Stage stage2 = new Stage();
        stage2.setTitle("Visualisation");
        stage2.setScene(new Scene(root2, 800, 480));
        stage2.initStyle(StageStyle.UNDECORATED);
        stage2.show();

        primaryStage.setOnCloseRequest(event -> stage2.hide());
    }

    @Override
    public void stop() throws Exception {
        try {
            System.out.println("Sent stop request to server.");
        } catch (Exception e) {
            System.out.println("Error stopping server.");
            e.printStackTrace();
        }
    }
}
