/**
 * Created by Mikaël on 2017-10-04.
 */
package builder;

import builder.plugins.StatisticsPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.TextonIo;
import io.TextonIoFactory;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import netscape.javascript.JSObject;
import org.apache.commons.text.StringEscapeUtils;
import textonclasses.Graph;
import textonclasses.TextonHeader;
import util.FXCustomDialogs;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO Ajouter un tooltip qui donne le nombre de liens entrants et sortants d'un noeud.
//TODO Ajouter un utilitaire de validation du graphe (peut être un plugin).
//À valider: Maximum 4 liens par texton
//Minimum 2 liens par texton
//Pas de lien vers soi-même (pas de boucle d'un texton vers lui-même)
//Le numéro des fichiers des textons correspond au numéro inscrit dans le json (dans le fichier).
//Pas deux textons musicaux qui se suivent
//Le texton d'accueil n'a pas de lien entrant.
//Pas de liens réciproques (commentaire: les liens réciproques ne veulent pas dire un aller-retour. Il pourra y avoir des liens réciproques 10-17.)
//... autres, au besoin.
public class BuilderVisContr {

    public JavaApplication javaApplication = new JavaApplication();
    private WebEngine webEngine;
    @FXML
    private Menu menuPlugins;
    @FXML
    private MenuItem menuOuvrir;
    @FXML
    private MenuItem menuRecharger;
    @FXML
    private MenuItem menuReconstruire;
    @FXML
    private MenuItem menuEnregistrer;
    @FXML
    private MenuItem menuEnregistrerSous;
    @FXML
    private MenuItem menuAjuster;
    @FXML
    private CheckMenuItem checkMenuPhysics;
    @FXML
    private WebView webView;
    @Inject
    private EventBus eventBus;
    @Inject
    private TextonIoFactory textonIoFactory;
    @Inject
    private Set<StatisticsPlugin> statPlugins;
    private Path path;
    private ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();
    private StringProperty jsonFromJavascript = new SimpleStringProperty();
    private BooleanProperty askForSaveOnJavascriptUpdate = new SimpleBooleanProperty(false);
    private EventHandler<WebEvent<String>> webAlertHandler = event -> {
        //Remplacer la fonction javascript alert() par un dialogue JavaFX.
        FXCustomDialogs.showError(event.getData());
        requestFocusAfterAlert();
    };
    private Callback<String, Boolean> webConfirmHandler = (Callback<String, Boolean>) param -> {
        //Remplacer la fonction javascript confirm() par un dialogue JavaFX.
        boolean b = FXCustomDialogs.showConfirmationAction(param);
        requestFocusAfterAlert();
        return b;
    };
    private Graph graph;
    private ChangeListener<String> jsonUpdateCallback = (observable, oldValue, newValue) -> {
        //This check is necessary to prevent stack overflows.
        if (newValue.equals("")) return;
        try {
            graph = Graph.createGraph(newValue);
        } catch (IOException e) {
            FXCustomDialogs.showError("La visualisation a envoyé un format invalide de json. Ceci est une erreur interne du programme.");
            e.printStackTrace();
            return;
        }
        jsonFromJavascript.set("");
        if (!askForSaveOnJavascriptUpdate.get()) return;
        try {
            askForSaveOnJavascriptUpdate.set(false);
            TextonIo textonIo = textonIoFactory.create(path);
            textonIo.saveGraph(graph);
        } catch (IOException e) {
            showBuilderVisError(e);
        }

    };

    private void requestFocusAfterAlert() {
        getStage().requestFocus();
    }

    Stage getStage() {
        return stageProperty.get();
    }

    @FXML
    void menuHandler(ActionEvent event) {
        if (event.getSource().equals(menuOuvrir)) {
            ouvrir();
        } else if (event.getSource().equals(menuRecharger)) {
            recharger();
        } else if (event.getSource().equals(menuReconstruire)) {
            reconstruire();
        } else if (event.getSource().equals(menuEnregistrer)) {
            enregistrer();
        } else if (event.getSource().equals(menuEnregistrerSous)) {
            enregistrerSous();
        } else if (event.getSource().equals(menuAjuster)) {
            menuAjuster();
        } else if (event.getSource().equals(checkMenuPhysics)) {
            //Do nothing, the behaviour is set in a listener declared in the initialize() method.
        }

    }

    private void reconstruire() {
        FileChooser fileChooser = new FileChooser();
        //FIXME Ceci ne marche pas si path est null. Pourtant, le MenuItem dit «reconstruire...», les trois points laissent entendre qu'un dialogue va suivre. Peut-être est-ce aussi la même chose avec les autres menus?
        fileChooser.setInitialDirectory(path.getParent().toFile());
        fileChooser.setTitle("Reconstruire le graphe");
        fileChooser.setInitialFileName("graph.json");
        ExtensionFilter filter = new ExtensionFilter("Fichier graphe .json", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) return;
        TextonIo textonIo = textonIoFactory.create(file.toPath());
        try {
            textonIo.validateGraph();
        } catch (IOException e) {
            FXCustomDialogs.showError("Impossible de reconstruire le graphe.");
            e.printStackTrace();
        }

    }

    private void ouvrir() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir le graphe");
        fileChooser.setInitialFileName("graph.json");
        if (path != null) fileChooser.setInitialDirectory(path.getParent().toFile());
        ExtensionFilter filter = new ExtensionFilter("Fichier json", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) return;
        TextonIo textonIo = textonIoFactory.create(file.toPath());
        try {
            graph = textonIo.getGraph();
            path = file.toPath();
            //webEngine.load(getClass().getResource("/public/builder/builder.html").toExternalForm());
            webEngine.load("http://localhost/builder/builder.html");
            webEngine.getLoadWorker().stateProperty().addListener(
                    (ov, oldState, newState) -> {
                        if (newState == State.SUCCEEDED) {
                            System.out.println("La page a été chargée avec succès.");
                            try {
                                loadGraphIntoWebView(graph);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            JSObject window = (JSObject) webEngine.executeScript("window");
                            window.setMember("javaFX", javaApplication);
                            webEngine.executeScript("console.log = function(msg) {\n" +
                                    "        javaFX.log(msg);\n" +
                                    "    };");
                        }
                    });
            menuEnregistrer.disableProperty().set(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recharger() {
        try {
            TextonIo textonIo = textonIoFactory.create(path);
            graph = textonIo.getGraph();
            loadGraphIntoWebView(graph);
        } catch (IOException e) {
            showBuilderVisError(e);
        }
    }

    private void loadGraphIntoWebView(Graph graph) throws JsonProcessingException {
        webEngine.executeScript("initGraphWithJson('" + StringEscapeUtils.escapeEcmaScript(new ObjectMapper().writeValueAsString(graph)) + "')");
    }

    private void enregistrer() {
        getGraphFromJsonDelayed(true);
    }

    private void enregistrerSous() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le graphe");
        fileChooser.setInitialFileName("graph.json");
        ExtensionFilter filter = new ExtensionFilter("Fichier .json", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        if (path != null)
            fileChooser.setInitialDirectory(path.toFile());
        File file = fileChooser.showSaveDialog(getStage());
        if (file == null) return;
        path = file.toPath();
        getGraphFromJsonDelayed(true);
    }

    private void menuAjuster() {
        webEngine.executeScript("fitGraph()");
    }

    private void getGraphFromJsonDelayed(boolean andThenSaveToPath) {
        askForSaveOnJavascriptUpdate.set(andThenSaveToPath);
        webEngine.executeScript("pushChanges()");
    }

    private void showBuilderVisError(Exception e) {
        FXCustomDialogs.showError("Échec de l'opération.");
        e.printStackTrace();
    }

    @FXML
    void initialize() {
        eventBus.register(this);
        Util.initializeStageRetriever(webView, stageProperty);
        webEngine = webView.getEngine();
        jsonFromJavascript.addListener(jsonUpdateCallback);
        menuEnregistrer.disableProperty().set(true);

        //Populate plugins menu
        menuPlugins.getItems().addAll(statPlugins.stream().map(statisticsPlugin -> {
            MenuItem menuItem = new MenuItem(statisticsPlugin.getName());
            menuItem.onActionProperty().set(event -> getPluginResultTable(statisticsPlugin));
            return menuItem;
        }).collect(Collectors.toList()));

        //Add confirm and alert listeners to WebEngine
        webEngine.onAlertProperty().set(webAlertHandler);
        webEngine.confirmHandlerProperty().set(webConfirmHandler);
        checkMenuPhysics.setSelected(true);
        checkMenuPhysics.selectedProperty().addListener((observable, oldValue, newValue) -> webEngine.executeScript("setPhysics(" + newValue + ")"));
    }

    private Node getPluginResultTable(StatisticsPlugin plugin) {
        plugin.init(graph);
        //If any of the prompts fails, return an empty node.
        if (plugin.getPrompts().stream().anyMatch((Pair<Function<String, Boolean>, String> functionStringPair) -> {
            String intro = "Le plugin " + plugin.getName() + " demande l'information suivante :\n";
            String input = FXCustomDialogs.showInput(intro + functionStringPair.getValue());

            //If there has been an error processing input
            if (!functionStringPair.getKey().apply(input)) {
                FXCustomDialogs.showError("Impossible d'initialiser le plugin " + plugin.getName());

                return true;
            }
            return false;
        })) return new AnchorPane();

        Map<TextonHeader, Double> pluginResults = plugin.compute();
        ObservableList<Entry<TextonHeader, Double>> pluginResultsAsList = FXCollections.observableArrayList(pluginResults.entrySet());

        TableColumn<Entry<TextonHeader, Double>, Integer> columnNum = new TableColumn<>("Numéro");
        columnNum.setCellValueFactory(param -> new SimpleObjectProperty<Integer>(param.getValue().getKey().getNumTexton()));

        TableColumn<Entry<TextonHeader, Double>, String> columnName = new TableColumn<>("Nom");
        columnName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey().getName()));

        TableColumn<Entry<TextonHeader, Double>, Double> columnStat = new TableColumn<>(plugin.getResultName());
        columnStat.setCellValueFactory(param -> new SimpleObjectProperty<Double>(param.getValue().getValue()));

        TableView<Entry<TextonHeader, Double>> table = new TableView<>(pluginResultsAsList);
        table.setEditable(false);
        table.getColumns().add(columnNum);
        table.getColumns().add(columnName);
        table.getColumns().add(columnStat);

        Label labelIntro = new Label("Résultats du plugin " + plugin.getName());
        labelIntro.setStyle("-fx-font-weight: bold");
        Label labelDesc = new Label(plugin.getResultDescription());
        Stream.of(labelIntro, labelDesc).forEach(label -> label.setWrapText(true));

        final VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(labelIntro, labelDesc, table);
        return vBox;
    }

    public class JavaApplication {
        public void receiveGraphJavaFX(String jsonGraph) {
            System.out.println("Valeur reçue de javascript:" + jsonGraph);
            jsonFromJavascript.set(jsonGraph);
        }

        public void log(String log) {
            System.out.println("---De javascript: " + log);
        }
    }
}
