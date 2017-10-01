package util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mikaël on 2017-09-24.
 */
public class TextonOverlayCanvasOnlyText extends TextonOverlayCanvas {

    private Graph graph;
    private boolean isTextonSet = false;
    private List<Text> texts = new ArrayList<>(4);

    private ChangeListener<Number> listener = (observable, oldValue, newValue) ->
            setBottomAnchorsOnTexts(newValue.doubleValue());

    private Runnable onSetTextonAction = () -> {
        //Remove old texts
        if(!(getParent() instanceof AnchorPane))
            throw new RuntimeException("The parent of a TextonOverlayCanvasOnlyText must be an instance of AnchorPane.");
        AnchorPane parent = (AnchorPane) getParent();
        parent.getChildren().removeAll(texts);

       texts = Arrays.stream(graph.getChildren(getTexton().getNumTexton())).mapToObj(i -> {
           String name = graph.getName(i);
           Text text = new Text(name);

           //TODO Vérifier les deux lignes suivantes et ajouter une citation.
           ObjectExpression<Font> fontTracking = Bindings.createObjectBinding(() -> Font.font(getWidth() / 4), widthProperty());
           text.fontProperty().bind(fontTracking);
           text.setFont(new Font(20));
           //text.setWrappingWidth(getWidth() - 40);
           return text;
       }).collect(Collectors.toList());

        setBottomAnchorsOnTexts(getHeight());
        if (!isTextonSet) {
            isTextonSet = true;
            System.out.println("listener added-----------------------");
            heightProperty().addListener(listener);
        }

        parent.getChildren().addAll(texts);
        texts.forEach(Node::toFront);

    };

    private void setBottomAnchorsOnTexts(double height) {
        for (int i = texts.size() - 1; i >= 0; i--) {
            double computed = height - ((i + 1)/texts.size()) * height;
            AnchorPane.setBottomAnchor(texts.get(i), computed * 0.4);
            System.out.println("///////////////////height = " + computed);
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
