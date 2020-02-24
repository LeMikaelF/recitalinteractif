package builder;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import guice.BuilderGuiceModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import server.Server;
import server.ServerRunnable;
import util.FXCustomDialogs;
import util.PropLoader;

import java.io.IOException;

/**
 * Created by Mikaël on 2017-10-04.
 */
public class BuilderMain extends Application implements Server {

    private final Injector guice;

    @Inject
    private
    ServerRunnable serverRunnable;

    public BuilderMain() {
        guice = Guice.createInjector(new BuilderGuiceModule());
        guice.injectMembers(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    static void preventCloseRequest(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();
            FXCustomDialogs.showError("Utilisez le menu « Affichage » de la fenêtre principale pour fermer cette fenêtre.");
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        BuilderVisContr builderVisContr = guice.getInstance(BuilderVisContr.class);
        BuilderContr builderContr = guice.getInstance(BuilderContr.class);

        Parent root2;
        Stage stage2 = new Stage();
        FXMLLoader loader2 = new FXMLLoader();
        loader2.setControllerFactory(param -> builderVisContr);
        loader2.setLocation(getClass().getResource("/fxml/BuilderVis.fxml"));
        root2 = loader2.load();

        stage2.setTitle("Visualisation du graphe");
        stage2.setScene(new Scene(root2));
        stage2.show();

        Parent root;
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> builderContr);
        loader.setLocation(getClass().getResource("/fxml/Builder.fxml"));
        root = loader.load();

        primaryStage.setTitle("Outil de lecture/écriture de textons");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            builderContr.hideChildrenStages();
        });
        preventCloseRequest(stage2);

        startServer();
    }

    @Override
    public void stop() {
        try {
            stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() throws Exception {
        serverRunnable.getServer().stop();
    }

    @Override
    public void startServer() {
        Thread t = new Thread(serverRunnable);
        t.setDaemon(true);
        t.start();
        ServerConnector connector = new ServerConnector(serverRunnable.getServer());
        int port = Integer.parseInt(PropLoader.getMap().get("port"));
        connector.setPort(port);
        connector.setHost("localhost");
        serverRunnable.getServer().setConnectors(new Connector[]{connector});
    }
}
