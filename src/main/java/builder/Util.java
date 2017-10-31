package builder;

import builder.plugins.StatisticsPlugin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import textonclasses.Graph;
import textonclasses.TextonHeader;
import util.FXCustomDialogs;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class Util {
    static Node getPluginResultTable(StatisticsPlugin plugin, Graph graph) {
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
        })) {
            return new AnchorPane();
        }

        Map<TextonHeader, Double> pluginResults = plugin.compute();
        ObservableList<Map.Entry<TextonHeader, Double>> pluginResultsAsList = FXCollections.observableArrayList(pluginResults.entrySet());

        TableColumn<Map.Entry<TextonHeader, Double>, Integer> columnNum = new TableColumn<>("Numéro");
        columnNum.setCellValueFactory(param -> new SimpleObjectProperty<Integer>(param.getValue().getKey().getNumTexton()));

        TableColumn<Map.Entry<TextonHeader, Double>, String> columnName = new TableColumn<>("Nom");
        columnName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey().getName()));

        TableColumn<Map.Entry<TextonHeader, Double>, Double> columnStat = new TableColumn<>(plugin.getResultName());
        columnStat.setCellValueFactory(param -> new SimpleObjectProperty<Double>(param.getValue().getValue()));

        TableView<Map.Entry<TextonHeader, Double>> table = new TableView<>(pluginResultsAsList);
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
}
