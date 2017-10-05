package builder;

/**
 * Created by Mikaël on 2017-10-04.
 */

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.TextonIo;
import io.XmlFileConnector;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import textonclasses.Texton;
import util.FXCustomDialogs;
import util.ResizableCanvasImpl;
import util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderContr {

    Path projectPath = null;
    @FXML
    private MenuItem menuNouveau;
    @FXML
    private MenuItem menuOuvrir;
    @FXML
    private MenuItem menuOuvrirImage;
    @FXML
    private MenuItem menuEnregistrer;
    @FXML
    private MenuItem menuEnregistrerSous;
    @FXML
    private MenuItem menuFermer;
    @FXML
    private CheckMenuItem menuCheckVisualisation;
    @FXML
    private CheckMenuItem menuCheckPreview;
    @FXML
    private TextField textFieldNum;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldSource;
    @FXML
    private TextField textAreaComment;
    @FXML
    private TextField textAreaDescription;
    @Inject
    private EventBus eventBus;

    @Inject
    private BuilderVisContr builderVisContr;
    private List<Stage> stageList = Stream.generate(Stage::new).limit(1).collect(Collectors.toList());
    private Texton texton;
    private File currentFile = null;
    private Image image = null;

    private List<TextInputControl> textInputControls = Stream.of(textFieldNum, textFieldName, textFieldSource, textAreaComment, textAreaDescription)
            .collect(Collectors.toList());

    private boolean updatedButNotSaved;
    private ChangeListener<String> numEnforcer =
            (observable, oldValue, newValue) -> {
                ((TextInputControl) ((StringProperty) observable).getBean())
                        .setText(newValue.replaceAll("[^\\d]", ""));
            };
    private ChangeListener<String> changeListenerUpdated = (observable, oldValue, newValue) -> {
        updatedButNotSaved = true;
        deactivateUpdatedListener();
    };
    private Stage stageImagePreview;
    private ResizableCanvasImpl canvasPreview;

    @FXML
    void menuHandler(ActionEvent event) {
        if (event.getSource().equals(menuNouveau)) {
            nouveau();
        } else if (event.getSource().equals(menuOuvrir)) {
            menuOuvrir();
        } else if (event.getSource().equals(menuOuvrirImage)) {
            ouvrirImage();
        } else if (event.getSource().equals(menuEnregistrer)) {
            //TODO Remettre updatedButNotSaved à false;
        } else if (event.getSource().equals(menuEnregistrerSous)) {
            //TODO Remettre updatedButNotSaved à false;
        } else if (event.getSource().equals(menuFermer)) {
            getStage().hide();
            hideChildrenStages();
        }
    }

    private void nouveau() {
        if (updatedButNotSaved)
            if (!checkWantsToAbandonChanges()) return;
        textInputControls.clear();
        menuCheckPreview.selectedProperty().set(false);
    }

    private void ouvrirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ajouter l'image du texton");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Fichiers image supportés",
                "*.jpg", "*.jpeg", "*.bmp", "*.wbmp", "*.png", "*.gif"));
        File imageFile = fileChooser.showOpenDialog(getStage());
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            image = SwingFXUtils.toFXImage(bufferedImage, null);
            canvasPreview.setImage(image);
            menuCheckPreview.selectedProperty().set(true);
        } catch (IOException e) {
            FXCustomDialogs.showError("Impossible d'ouvrir l'image.");
            e.printStackTrace();
        }
    }

    private void menuEnregistrer() {
        if (image == null) {
            FXCustomDialogs.showError("Vous devez sélectionné une image pour le texton.");
        }
        Texton save = new Texton(Integer.parseInt(textFieldNum.textProperty().get()), textFieldSource.textProperty().get(),
                textFieldName.textProperty().get(), textAreaDescription.textProperty().get(), textAreaComment.textProperty().get(), image);
        //TODO Ajouter une méthode de sélection d'image.

        try {
            new XmlFileConnector(new String[]{currentFile.toString()}).writeTexton(save, true);
        } catch (IOException | URISyntaxException e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
        deactivateUpdatedListener();
    }

    private void menuEnregistrerSous() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le texton");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Texton au format json", "*.json"));
        currentFile = fileChooser.showSaveDialog(getStage());
        deactivateUpdatedListener();
    }

    private void menuOuvrir() {
        if (updatedButNotSaved) {
            if (!checkWantsToAbandonChanges()) {
                //L'utilisateur a fait des changements, mais ne veut pas les abandonner.
                return;
            }
        }
        menuEnregistrer.disableProperty().set(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir le texton");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Texton au format json", "*.json"));
        File file = fileChooser.showOpenDialog(getStage());

        //Check that filename is well-formed.
        if (!file.toPath().getFileName().toString().matches("[0-9][0-9][0-9][.]json")) {
            //Filename is not well-formed.
            FXCustomDialogs.showError("Le nom du fichier est invalide. Les fichiers doivent correspondre au format \"000.json\".");
            return;
        }

        projectPath = file.toPath().getParent();
        try {
            TextonIo textonIo = new XmlFileConnector(new String[]{file.toString()});
            texton = textonIo.readTexton(Integer.parseInt(file.toPath().getFileName().toString().substring(0, 3)));
            chargerTexton();
            activateUpdatedListener();
            currentFile = file;
        } catch (IOException e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Il y a eu une erreur en construisant le TextonIo. Ceci est un problème interne au programme.");
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
    }

    private boolean checkWantsToAbandonChanges() {
        return FXCustomDialogs.showConfirmationAction("Des champs ont été modifiés depuis le dernier enregistrement. " +
                "Voulez-vous abandonner vos changements?");
    }

    private void chargerTexton() {
        textFieldNum.textProperty().set(texton.getNumTexton().toString());
        textFieldName.textProperty().set(texton.getName());
        textFieldSource.textProperty().set(texton.getSource());
        textAreaComment.textProperty().set(texton.getComment());
        textAreaDescription.textProperty().set(texton.getDescription());
    }

    private void activateUpdatedListener() {
        textInputControls.forEach(textInputControl -> textInputControl.textProperty().addListener(changeListenerUpdated));
    }

    private void deactivateUpdatedListener() {
        textInputControls.forEach(textInputControl -> textInputControl.textProperty().removeListener(changeListenerUpdated));
    }

    private void createImagePreview() {
        stageImagePreview = new Stage();
        stageImagePreview.setTitle("Visualisation de l'image");
        canvasPreview = new ResizableCanvasImpl();
        Scene scene = new Scene(new AnchorPane(canvasPreview), 370, 208);
        stageImagePreview.setScene(scene);
        BuilderMain.preventCloseRequest(stageImagePreview);
    }

    @FXML
    void initialize() {
        eventBus.register(this);
        menuEnregistrer.disableProperty().set(true);
        textFieldNum.textProperty().addListener(numEnforcer);
        //someProperty.bind(menuCheckVisualisation.selectedProperty());
        Util.initializeStageRetriever(textFieldNum, stageList);
        createImagePreview();

        menuCheckVisualisation.selectedProperty().set(true);
        menuCheckPreview.selectedProperty().set(true);
        addShowHideCapability(menuCheckVisualisation.selectedProperty(), builderVisContr.getStage());
        addShowHideCapability(menuCheckPreview.selectedProperty(), stageImagePreview);
    }

    private void addShowHideCapability(BooleanProperty controller, Stage stage) {
        controller.addListener((observable, oldValue, newValue) -> {
            if (newValue) stage.show();
            else stage.hide();
        });
    }

    private Stage getStage() {
        return stageList.get(0);
    }

    public void hideChildrenStages(){
        stageImagePreview.hide();
        builderVisContr.getStage().hide();
    }

}
