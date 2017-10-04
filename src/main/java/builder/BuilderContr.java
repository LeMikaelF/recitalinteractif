package builder;

import io.TextonIo;
import io.XmlFileConnector;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import textonclasses.Texton;
import textonclasses.TextonLien;
import util.FXCustomDialogs;
import textonclasses.Graph;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Mikaël on 2017-01-07.
 */
public class BuilderContr {
    //TODO Accéder à la base de donnée de manière concurrente, en affichant un dialogue de chargement, pour ne pas geler l'interface graphique.
    private static BooleanProperty writeToNextAvailProperty = new SimpleBooleanProperty(false);
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private VBox vBoxControles;
    @FXML
    private TextField textFieldNum;
    @FXML
    private TextField textFieldMinuterie;
    @FXML
    private TextField textFieldNom;
    @FXML
    private TextField textFieldSource;
    @FXML
    private TextArea textAreaDesc;
    @FXML
    private MenuItem menuNouveau;
    @FXML
    private MenuItem menuSelectionner;
    @FXML
    private MenuItem menuOuvrir;
    @FXML
    private MenuItem menuEnregistrer;
    @FXML
    private MenuItem menuReinitialiser;
    @FXML
    private MenuItem menuFermer;
    @FXML
    private CheckBox checkBoxAuto;

    private Stage stage;
    private TextonIo textonIo = new XmlFileConnector(new String[]{"C:\\Textons\\"});
    private Graph graph = textonIo.getGraph();
    private List<TextInputControl> textContainers;
    private Image image;

    private List<TextInputControl> numericFields;
    private ChangeListener<String> numEnforcer =
            (observable, oldValue, newValue) -> ((TextInputControl) ((StringProperty) observable).getBean())
                    .setText(newValue.replaceAll("[^\\d]", ""));

    public BuilderContr() throws IOException, URISyntaxException {
    }

    @FXML
    void menuHandler(ActionEvent event) {
        if (event.getSource() == menuNouveau) {
            nouveau();
        } else if (event.getSource() == menuSelectionner) {
            menuSelectionner();
        } else if (event.getSource() == menuOuvrir) {
            ouvrirTexton();
        } else if (event.getSource() == menuEnregistrer) {
            saveTexton();
        } else if (event.getSource() == menuReinitialiser) {
            resetBuilder();
        } else if (event.getSource() == menuFermer) {
            stage.close();
        }
    }

    private void menuSelectionner() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez une image pour le texton");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image png", "*.png"));
        File tempFile = fileChooser.showOpenDialog(stage);
        if (tempFile == null) return;
        System.out.println("tempFile = " + tempFile.toString());
        setImage(tempFile);
    }

    private void nouveau() {
        if (!textContainers.stream().allMatch(textInputControl -> textInputControl.getText().matches("")))
            if (!FXCustomDialogs.showConfirmationAction("Un texton est déjà chargé et n'a peut-être pas été sauvegardé. Voulez-vous l'abandonner?"))
                //L'utilisateur ne veut pas abandonner le texton.
                return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez une image pour le nouveau texton");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image png", "*.png"));
        File tempFile = fileChooser.showOpenDialog(stage);
        if (tempFile == null) return;
        System.out.println("tempFile = " + tempFile.toString());
        resetBuilder();
        setImage(tempFile);
    }

    private void setImage(File file) {
        Image image = null;
        try {
            image = new Image(file.toURI().toURL().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTexton() {
        List<TextonLien> textonLiens = new ArrayList<>();
        Integer numTexton = Integer.valueOf(textFieldNum.getText());

        Texton save = new Texton(numTexton, textFieldSource.getText(), textFieldNom.getText(), textAreaDesc.getText(), "", getImage());

        try {
            textonIo.writeTexton(save, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void ouvrirTexton() {
        FXCustomDialogs.showOpenTextonDialog(this::loadTexton);
    }

    private void loadTexton(int numTexton) {
        try {
            Texton texton = textonIo.readTexton(numTexton);
            resetBuilder();
            textFieldNum.setText(texton.getNumTexton().toString());
            textFieldNom.setText(texton.getName());
            textFieldSource.setText(texton.getSource());
            textAreaDesc.setText(texton.getDescription());
        } catch (IOException e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
    }

    private void resetBuilder() {
        System.out.println(textContainers);
        textContainers.forEach(TextInputControl::clear);
    }

    @FXML
    void initialize() {
        initializeStageRetriever();

        textContainers = buildTextContainers();
        numericFields = buildNumericFields();
        numericFields.forEach(textInputControl -> textInputControl.textProperty().addListener(numEnforcer));
        textFieldNum.disableProperty().bind(checkBoxAuto.selectedProperty());
        writeToNextAvailProperty.bind(checkBoxAuto.selectedProperty());

    }

    private void initializeStageRetriever() {
        //get Stage as soon as it's initialized
        textAreaDesc.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue == null) return;
                newValue.windowProperty().addListener(new ChangeListener<Window>() {
                    @Override
                    public void changed(ObservableValue<? extends Window> observable1, Window oldValue1, Window newValue1) {
                        if (newValue1 == null) return;
                        stage = (Stage) newValue1;
                        newValue.windowProperty().removeListener(this);
                    }
                });
                textAreaDesc.sceneProperty().removeListener(this);
            }
        });
    }

    private List<TextInputControl> buildTextContainers() {
        List<TextInputControl> list = new ArrayList<>();
        list.add(textFieldNum);
        list.add(textFieldNom);
        list.add(textFieldSource);
        list.add(textFieldMinuterie);
        list.add(textAreaDesc);
        return list;
    }

    private List<TextInputControl> buildNumericFields() {
        List<TextInputControl> list = new ArrayList<>();
        list.add(textFieldNum);
        list.add(textFieldMinuterie);
        return list;
    }

    public Image getImage() {
        return image;
    }
}