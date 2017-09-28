package presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.Server;

public class MainPres extends Application {
    private static ContrPres contrPres;
    private Stage stage2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> {
            if (contrPres == null) contrPres = new ContrPres();
            return contrPres;
        });
        loader.setLocation(getClass().getResource("/TableauBord.fxml"));
        root = loader.load();
        primaryStage.setTitle("Tableau de bord de récital");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        Parent root2;
        stage2 = new Stage();
        FXMLLoader loader2 = new FXMLLoader();
        loader2.setControllerFactory(param -> contrPres);
        loader2.setLocation(getClass().getResource("/Visualisation.fxml"));
        root2 = loader2.load();
        stage2.setTitle("Visualisation");
        stage2.setScene(new Scene(root2, 800, 480));
        stage2.initStyle(StageStyle.UNDECORATED);
        stage2.show();

        primaryStage.setOnCloseRequest(event -> stage2.hide());
        contrPres.setStageTableau(primaryStage);
        contrPres.setStageVis(stage2);
    }

    @Override
    public void stop() throws Exception {
        try {
            Server.stopServer();
            System.out.println("Sent stop request to server.");
        } catch (Exception e) {
            System.out.println("Error stopping server.");
            e.printStackTrace();
        }
    }
}
