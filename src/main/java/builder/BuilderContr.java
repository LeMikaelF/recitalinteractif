package builder;

/**
 * Created by Mikaël on 2017-10-04.
 */

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.TextonIo;
import io.TextonIoFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import textonclasses.Texton;
import util.CanvasUtil;
import util.FXCustomDialogs;
import util.ResizableCanvasImpl;
import util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO L'image dépasse un peu des côtés des fois (quand on change la fenêtre de taille, l'algorithme réagit mal?).
public class BuilderContr {

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
    private TextArea textAreaComment;
    @FXML
    private TextArea textAreaDescription;
    @Inject
    private EventBus eventBus;
    @Inject
    private TextonIoFactory textonIoFactory;
    @Inject
    private BuilderVisContr builderVisContr;
    private Path projectPath;
    private final ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();
    private Texton texton;
    private File currentFile;
    private Image image;

    private List<TextInputControl> textInputControls;

    private boolean updatedButNotSaved;
    private final ChangeListener<String> changeListenerUpdated = (observable, oldValue, newValue) -> {
        updatedButNotSaved = true;
        deactivateUpdatedListener();
    };
    private final ChangeListener<String> numEnforcer =
            (observable, oldValue, newValue) -> {
                ((TextInputControl) ((StringProperty) observable).getBean())
                        .setText(newValue.replaceAll("[^\\d]", ""));
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
            menuEnregistrer();
        } else if (event.getSource().equals(menuEnregistrerSous)) {
            menuEnregistrerSous();
        } else if (event.getSource().equals(menuFermer)) {
            getStage().hide();
            hideChildrenStages();
        }
    }

    private void nouveau() {
        if (updatedButNotSaved)
            if (!checkWantsToAbandonChanges()) return;
        textInputControls.forEach(textInputControl -> textInputControl.textProperty().set(""));
        canvasPreview.setImage(null);
    }

    private void ouvrirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ajouter l'image du texton");
        ExtensionFilter filter = new ExtensionFilter("Fichiers image supportés",
                "*.jpg", "*.jpeg", "*.bmp", "*.wbmp", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        File imageFile = fileChooser.showOpenDialog(getStage());
        if (imageFile == null) return;
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
        Texton save = buildTextonFromFields();

        try {
            if (currentFile == null) throw new IOException();
            textonIoFactory.create(currentFile.toPath()).writeTexton(save, true);
        } catch (IOException e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
        deactivateUpdatedListener();
    }

    private void menuEnregistrerSous() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(Util.getFormattedNumSerie(Integer.parseInt(textFieldNum.textProperty().get())) + ".json");
        if (currentFile != null) fileChooser.setInitialDirectory(currentFile.getParentFile());
        fileChooser.setTitle("Enregistrer le texton");
        ExtensionFilter filter = new ExtensionFilter("Texton au format json", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        File tempFile = fileChooser.showSaveDialog(getStage());
        if (tempFile != null) currentFile = tempFile;
        try {
            if (currentFile == null) throw new IOException();
            buildTextonFromFields();
            textonIoFactory.create(currentFile.toPath()).writeTexton(buildTextonFromFields(), true);
            deactivateUpdatedListener();
        } catch (IOException e) {
            FXCustomDialogs.showError("Échec de l'enregistrement.");
            e.printStackTrace();
        }
    }

    private Texton buildTextonFromFields() {
        texton = new Texton(Integer.parseInt(textFieldNum.textProperty().get()), textFieldSource.textProperty().get(),
                textFieldName.textProperty().get(), textAreaDescription.textProperty().get(),
                textAreaComment.textProperty().get(), image);
        return texton;
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
        if (currentFile != null) fileChooser.setInitialDirectory(currentFile.getParentFile());
        fileChooser.setTitle("Ouvrir le texton");
        ExtensionFilter filter = new ExtensionFilter("Texton au format json", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) return;

        //Check that filename is well-formed.
        if (!file.toPath().getFileName().toString().matches("[0-9][0-9][0-9][.]json")) {
            //Filename is not well-formed.
            FXCustomDialogs.showError("Le nom du fichier est invalide. Les fichiers doivent correspondre au format \"000.json\".");
            return;
        }

        projectPath = file.toPath().getParent();
        try {
            TextonIo textonIo = textonIoFactory.create(file.toPath());
            texton = textonIo.readTexton(Integer.parseInt(file.toPath().getFileName().toString().substring(0, 3)));
            chargerTexton();
            activateUpdatedListener();
            currentFile = file;
        } catch (IOException e) {
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
        canvasPreview.setImage(texton.getImage());
    }

    private void activateUpdatedListener() {
        textInputControls.forEach(textInputControl -> textInputControl.textProperty().addListener(changeListenerUpdated));
    }

    private void deactivateUpdatedListener() {
        updatedButNotSaved = false;
        textInputControls.forEach(textInputControl -> textInputControl.textProperty().removeListener(changeListenerUpdated));
    }

    private void createImagePreview() {
        stageImagePreview = new Stage();
        stageImagePreview.setTitle("Visualisation de l'image");
        canvasPreview = new ResizableCanvasImpl();
        Scene scene = new Scene(new AnchorPane(canvasPreview), 370, 208);
        CanvasUtil.setNodeAnchorToAnchorPane(canvasPreview, 0, 0, 0, 0);
        stageImagePreview.setScene(scene);
        BuilderMain.preventCloseRequest(stageImagePreview);
        stageImagePreview.setResizable(false);
        stageImagePreview.show();
    }

    @FXML
    void initialize() {
        eventBus.register(this);
        menuEnregistrer.disableProperty().set(true);
        textFieldNum.textProperty().addListener(numEnforcer);
        //someProperty.bind(menuCheckVisualisation.selectedProperty());
        Util.initializeStageRetriever(textFieldNum, stageProperty);
        createImagePreview();

        menuCheckVisualisation.selectedProperty().set(true);
        menuCheckPreview.selectedProperty().set(true);
        addShowHideCapability(menuCheckVisualisation.selectedProperty(), builderVisContr.getStage());
        addShowHideCapability(menuCheckPreview.selectedProperty(), stageImagePreview);

        textInputControls = Stream.of(textFieldNum, textFieldName, textFieldSource, textAreaComment, textAreaDescription)
                .collect(Collectors.toList());
        activateUpdatedListener();

        Stream.of(textAreaComment, textAreaDescription).forEach(textArea -> textArea.setWrapText(true));
    }

    private void addShowHideCapability(BooleanProperty controller, Stage stage) {
        controller.addListener((observable, oldValue, newValue) -> {
            if (newValue) stage.show();
            else stage.hide();
        });
    }

    private Stage getStage() {
        return stageProperty.get();
    }

    public void hideChildrenStages() {
        stageImagePreview.hide();
        builderVisContr.getStage().hide();
    }

}
