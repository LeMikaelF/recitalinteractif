package presentation;

import builder.BuilderMain;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import events.ControlEvent;
import guice.ServletGuiceModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import guice.FxGuiceModule;

import java.io.IOException;
//TODO Package program in portable executable for Windows/Apple
//TODO Replace config file with command line arguments
//TODO Have «Commencer récital» ask for graph.json location
public class Main extends Application {
    private final Injector guice = Guice.createInjector(new FxGuiceModule(), new ServletGuiceModule());

    public static void main(String[] args) {
        dispatchFromArgs(args);
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
    public void stop() {
        try {
            guice.getInstance(EventBus.class).post(ControlEvent.SHUTDOWN);
        } catch (Exception e) {
            System.out.println("Error stopping server.");
            e.printStackTrace();
        }
    }

    private static void dispatchFromArgs(String[] args){
        if(args.length == 0) launch(args);
        else if (args[0].equals("-b") || args[0].equals("--builder")) BuilderMain.main(new String[]{});
    }
}
