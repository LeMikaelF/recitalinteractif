package util;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Mikaël on 2016-11-02.
 */
public class FXCustomDialogs {
    //Les méthodes showException, showConfirmationAction, showError, showInput et showInfoSimple(String message)
    //sont adaptées de Jakob, Marco. 2014. « JavaFX Dialogs (official) ». code.makery.
    //28 octobre. Consulté le 24 décembre 2016. <http://code.makery.ch/blog/javafx-dialogs-official/>.

    public static void showException(Exception passedException) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Une exception est survenue.");
        alert.setContentText(passedException.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        passedException.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static boolean showConfirmationAction(String str) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("");
        alert.setContentText(str);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && (result.get() == ButtonType.OK);
    }

    public static void showError(String str) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(str);
        alert.showAndWait();
    }

    public static String showInput(String message) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Entrée d'informations");
        dialog.setHeaderText("");
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) return result.get();
        else return null;
    }

    public static void showInfoSimple(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void displayInDialog(Node display, String title) {
        Dialog alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.getDialogPane().setContent(new VBox(display));
        alert.setResizable(true);
        alert.showAndWait();
    }

    public static Node getListDisplayPane(String string) {
        TextArea textArea = new TextArea(string);
        textArea.setWrapText(true);
        return textArea;
    }

    public static <T> String ListToString(List<T> rectList, Class<T> clazz){
        return rectList.toString().replace(", ", "\n").replace(clazz.getSimpleName() + " [",
                "---Élément " + "---\n").replace("[", "").replace("]", "\n");


    }

    public static void showOpenTextonDialog(Consumer<Integer> methodToloadTexton) {
        String choix = FXCustomDialogs.showInput("Naviguer vers le texton…");
        if (choix != null) {
            int numChoisi = Integer.parseInt(choix);
            if ( Util.isInteger(choix) && Integer.parseInt(choix) > 0 && Integer.parseInt(choix) <= 1000) {
                try {
                    methodToloadTexton.accept(numChoisi);
                } catch (Exception e) {
                    FXCustomDialogs.showException(e);
                    e.printStackTrace();
                }
            }
        }
    }

}


