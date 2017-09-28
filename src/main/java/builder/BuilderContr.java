package builder;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import sql.SqlConnector;
import textonclasses.*;
import util.FXCustomDialogs;
import util.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Mikaël on 2017-01-07.
 */
public class BuilderContr {
    //TODO Accéder à la base de donnée de manière concurrente, en affichant un dialogue de chargement, pour ne pas geler l'interface graphique.
    public static BooleanProperty writeToNextAvailProperty = new SimpleBooleanProperty(false);
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private VBox vBoxControles;
    @FXML
    private TextField textFieldNum;
    @FXML
    private ChoiceBox<String> choiceBoxType;
    @FXML
    private TextField textFieldMinuterie;
    @FXML
    private TextField textFieldNom;
    @FXML
    private TextField textFieldSource;
    @FXML
    private TextArea textAreaDesc;
    @FXML
    private RadioButton radio0;
    @FXML
    private RadioButton radio1;
    @FXML
    private ToggleGroup ToggleGroupNumLiens;
    @FXML
    private RadioButton radio2;
    @FXML
    private RadioButton radio3;
    @FXML
    private RadioButton radio4;
    @FXML
    private TextField textFieldLien1;
    @FXML
    private TextField textFieldLien2;
    @FXML
    private TextField textFieldLien3;
    @FXML
    private TextField textFieldLien4;
    @FXML
    private MenuItem menuNouveau;
    @FXML
    private MenuItem menuSelectionner;
    @FXML
    private MenuItem menuOuvrir;
    @FXML
    private MenuItem menuEnregistrer;
    @FXML
    private MenuItem menuAfficherCoord;
    @FXML
    private MenuItem menuAfficherLiensPendants;
    @FXML
    private MenuItem menuReinitialiser;
    @FXML
    private MenuItem menuFermer;
    @FXML
    private CheckBox checkBoxAuto;

    private Stage stage;
    private IntegerProperty numLiens = new SimpleIntegerProperty(4);
    private BufferedImage bImage;
    private VisContr visContr = new VisContr(800, 600);
    private TextonReaderWriter textonIO = new SqlConnector();
    private List<TextInputControl> textContainers;
    private List<TextInputControl> targetContainers;
    private List<RadioButton> radios;
    private List<TextInputControl> numericFields;
    private ChangeListener<String> numEnforcer =
            (observable, oldValue, newValue) -> ((TextInputControl) ((StringProperty) observable).getBean())
                    .setText(newValue.replaceAll("[^\\d]", ""));

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
        } else if (event.getSource() == menuAfficherCoord) {
            afficherCoord();
        } else if (event.getSource() == menuAfficherLiensPendants) {
            afficherLiensPendants();
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
            visContr.setImage(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //bImage = SwingFXUtils.fromFXImage(new Image(file.toString()), null);*/
        bImage = SwingFXUtils.fromFXImage(image, null);
    }

    private void afficherLiensPendants() {
        ArrayList<String> listToObserve;
        try {
            List<List<Integer>> sourceAndDestList = textonIO.getHangingLinks();
            Map<Integer, String> numToName = textonIO.getTextonNumsToNamesMap(sourceAndDestList.get(0));
            listToObserve = new ArrayList<>(IntStream.range(0, sourceAndDestList.size()).mapToObj(
                    i -> sourceAndDestList.get(0).get(i).toString().concat("\t").concat(numToName.get(sourceAndDestList.get(0).get(i))).concat("\t").concat(sourceAndDestList.get(1).get(i).toString())
            ).collect(Collectors.toList()));
        } catch (SQLException e) {
            listToObserve = new ArrayList<>();
            e.printStackTrace();
        }
        FXCustomDialogs.displayInDialog(new ListView<>(FXCollections.observableList(listToObserve)), "Liens pendants\n(numéro d'origine + nom du texton d'origine + numéro de destination");
    }

    private void afficherCoord() {
        FXCustomDialogs.displayInDialog(FXCustomDialogs.getListDisplayPane(FXCustomDialogs.ListToString(visContr.getPercentRectangles(), Rectangle2D.class)), "Coordonnées en pourcentage des liens");
    }

    private void saveTexton() {
        ArrayList<LienTexton> lienTextons;
        try {
            lienTextons = new ArrayList<>(IntStream.range(0, numLiens.get()).mapToObj(i -> new LienTexton(
                    Integer.valueOf(targetContainers.get(i).getText()), null, null, visContr.getPercentRectangles()
                    .get(i))).collect(Collectors.toList()));
        } catch (Exception e) {
            FXCustomDialogs.showError("Format des liens incorrect. Texton non sauvegardé.");
            return;
        }
        System.out.println("lienTextons : " + lienTextons);
        try {
            textonIO.writeTexton(new TextonBuilder().setNumTexton(Integer.parseInt(textFieldNum.getText()))
                    .setName(textFieldNom.getText())
                    .setType(TextonType.valueOf(choiceBoxType.getValue()))
                    .setTimer(Integer.parseInt(textFieldMinuterie.getText().isEmpty() ? "0" : textFieldMinuterie.getText()))
                    .setSource(textFieldSource.getText())
                    .setDescription(textAreaDesc.getText())
                    .setLienTexton(lienTextons)
                    .setbImage(bImage)
                    .createTexton(), writeToNextAvailProperty.get());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            FXCustomDialogs.showException(e);
        }
    }

    private void ouvrirTexton() {
        FXCustomDialogs.showOpenTextonDialog(this::loadTexton);
    }

    private void loadTexton(int numTexton) {
        try {
            visContr.resetRectangles();
            Texton texton = textonIO.readTexton(numTexton);
            resetBuilder();
            bImage = texton.getBimage();
            visContr.setTexton(texton);
            textFieldNum.setText(texton.getNumTexton().toString());
            choiceBoxType.setValue(texton.getType().toString());
            textFieldMinuterie.setText(texton instanceof TextonV ? String.valueOf(((TextonV) texton).getTimer()) : "");
            textFieldNom.setText(texton.getName());
            textFieldSource.setText(texton.getSource());
            textAreaDesc.setText(texton.getDescription());
            numLiens.set(texton.getLienTexton().size());
            radios.get(numLiens.get()).selectedProperty().set(true);
            IntStream.range(0, numLiens.get())
                    .forEach(i -> targetContainers.get(i).setText(String.valueOf(texton.getLienTexton().get(i).getNumSerie())));
        } catch (SQLException | IOException e) {
            FXCustomDialogs.showException(e);
            e.printStackTrace();
        }
    }

    private void resetBuilder() {
        System.out.println(textContainers);
        visContr.setTexton(null);
        visContr.resetRectangles();
        textContainers.forEach(TextInputControl::clear);
    }

    @FXML
    void initialize() {
        initializeStageRetriever();

        visContr.numRectProperty().bind(numLiens);

        choiceBoxType.getItems().addAll(Util.getNames(TextonType.class));
        choiceBoxType.setValue(choiceBoxType.getItems().get(0));

        textContainers = buildTextContainers();
        targetContainers = buildTargetContainers();
        textContainers.addAll(targetContainers);
        radios = buildRadios();
        numericFields = buildNumericFields();
        //Bind radio buttons to target textfields and visualization
        IntStream.range(0, targetContainers.size()).forEach(i -> targetContainers.get(i).disableProperty().bind(Bindings.greaterThan(numLiens, i).not()));
        //IntStream.range(0, radios.size()).forEach(i -> radios.get(i).selectedProperty().addListener((observable, oldValue, newValue) -> numLiens.set(i + 1)));
        IntStream.range(0, radios.size()).forEach(i -> radios.get(i).selectedProperty().addListener((observable, oldValue, newValue) -> numLiens.set(i)));

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

                        //Close visContr when main stage is closed.
                        stage = (Stage) newValue1;
                        stage.setOnCloseRequest(event -> visContr.getStage().close());

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

    private List<TextInputControl> buildTargetContainers() {
        List<TextInputControl> list = new ArrayList<>();
        list.add(textFieldLien1);
        list.add(textFieldLien2);
        list.add(textFieldLien3);
        list.add(textFieldLien4);
        return list;
    }

    private List<RadioButton> buildRadios() {
        List<RadioButton> radios = new ArrayList<>();
        radios.add(radio0);
        radios.add(radio1);
        radios.add(radio2);
        radios.add(radio3);
        radios.add(radio4);
        return radios;
    }

    private List<TextInputControl> buildNumericFields() {
        List<TextInputControl> list = new ArrayList<>();
        list.add(textFieldNum);
        list.add(textFieldMinuterie);
        list.addAll(targetContainers);
        return list;
    }

}