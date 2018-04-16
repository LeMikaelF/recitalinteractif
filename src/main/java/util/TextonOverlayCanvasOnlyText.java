package util;

import javafx.beans.value.ChangeListener;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import server.Vote;
import textonclasses.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mikaël on 2017-09-24.
 */

//J'ai choisi de diminuer la grosseur du texte parce que je n'étais capable de gérer le word-wrap avec des TextFlow.
public class TextonOverlayCanvasOnlyText extends TextonOverlayCanvas {

    private Graph graph;
    private boolean isTextonSet;
    private List<Text> texts = new ArrayList<>(4);
    private List<TextFlow> textFlows = new ArrayList<>();
    private List<Text> descriptors = new ArrayList<>();
    private Runnable onSetTextonAction = () -> {
        if (!(getParent() instanceof AnchorPane)) {
            throw new RuntimeException("The parent of a TextonOverlayCanvasOnlyText must be an instance of AnchorPane.");
        }
        AnchorPane parent = (AnchorPane) getParent();
        parent.getChildren().removeAll(textFlows);
        textFlows.clear();
        texts.clear();

        //Construire les Text des titres des liens
        texts = graph.getChildren(getTexton().getNumTexton()).stream().map(i -> {
            String name = graph.getName(i);
            Text text = new Text(name);
            return text;
        }).limit(4).collect(Collectors.toList());

        //Generate TextFlow list to put descriptors and texts together.
        for (int i = 0; i < texts.size(); i++) {
            textFlows.add(new TextFlow(descriptors.get(i), texts.get(i)));
        }

        textFlows.forEach(textFlow -> {
            //For readability
            DropShadow shadow = new DropShadow(10, 0, 0, Color.WHITE);

            //Pour que l'ombre soit plus visible.
            DropShadow shadow2 = new DropShadow(10, 0, 0, Color.WHITE);
            shadow.setInput(shadow2);
            DropShadow shadow3 = new DropShadow(10, 0, 0, Color.WHITE);
            shadow2.setInput(shadow3);

            textFlow.setEffect(shadow);

            textFlow.getChildren().forEach(node -> ((Text) node).setFont(Font.font(20)));
            textFlow.prefWidthProperty().bind(widthProperty().subtract(getWidth() / 15).subtract(getWidth() / 15));

            ChangeListener<Number> widthChangeListener = (observable, oldValue, newValue) -> {
                //Ceci n'est pas idéal pour centrer le texte.
                AnchorPane.setLeftAnchor(textFlow, getWidth() / 4);
            };

            textFlow.prefWidthProperty().addListener(widthChangeListener);
            //Run the listener for the first time.
            widthChangeListener.changed(textFlow.prefWidthProperty(), 0, 0);
            textFlow.toFront();
        });

        /*//Change font size dynamically
        ChangeListener<Number> widthTextFontListener = (observable, oldValue, newValue) ->
                textFlows.forEach(textFlow -> textFlow.getChildren().forEach(node -> {
                    ((Text) node).setFont(Font.font(getWidth() / 30));
                }));
        widthProperty().addListener(widthTextFontListener);
        widthTextFontListener.changed(null, null, getWidth());*/

        //Recalculate bottom offsets whenever height is changed.
        heightProperty().addListener((observable, oldValue, newValue) -> setBottomAnchorsOnTextFlows(newValue.doubleValue()));
        //Run the height listener on initialization.
        parent.getChildren().addAll(textFlows);
        setBottomAnchorsOnTextFlows(getHeight());

        if (!isTextonSet) {
            isTextonSet = true;
        }
    };

    TextonOverlayCanvasOnlyText() {
        //Generate colored link descriptors
        Vote[] voteArray = Vote.values();
        //We need to exclude last Vote because its value is NULL.
        for (int i = 0; i < voteArray.length - 1; i++) {
            Text descriptor = new Text("Lien " + voteArray[i] + " : ");
            descriptor.setFill(CanvasUtil.getLinkToColorMap().get(i));
            descriptors.add(descriptor);
        }
    }

    private void setBottomAnchorsOnTextFlows(double height) {
        double totalLayoutHeights = height / 40;
        for (int i = textFlows.size() - 1; i >= 0; i--) {
            AnchorPane.setBottomAnchor(textFlows.get(i), totalLayoutHeights);
            totalLayoutHeights += textFlows.get(i).getBoundsInParent().getHeight() + height / 60;
        }
    }

    @Override
    public void draw() {
        clearCanvas();
    }

    @Override
    protected Runnable onSetTexton() {
        return onSetTextonAction;
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
