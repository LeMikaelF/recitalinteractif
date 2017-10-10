package presentation;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import events.ScreenDispatchEvent;
import javafx.collections.ObservableList;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Mikaël on 2017-10-09.
 */
public class ScreenDispatcher {

    private static BiConsumer<Stage, Integer> setupToScreen = (stage, value) -> {
        ObservableList<Screen> screens = Screen.getScreens();
        stage.setX(screens.get(value) != null ? screens.get(value).getVisualBounds().getMinX() : 0);
        stage.setY(screens.get(value) != null ? screens.get(value).getVisualBounds().getMinY() : 0);
        stage.setWidth(screens.get(value) != null ? screens.get(value).getBounds().getWidth() : 0);
        stage.setHeight(screens.get(value) != null ? screens.get(value).getBounds().getHeight() : 0);
        stage.setMaximized(true);
        stage.toFront();
    };
    @Inject
    EventBus eventBus;

    private static boolean isEnvironmentRight(Stage stage) {
        ObservableList<Screen> screens = Screen.getScreens();
        //If there is only one screen, action cannot be performed.
        if (screens.size() != 2) {
            return false;
        }

        ObservableList<Screen> screensContainingTabBord = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        if (screensContainingTabBord.size() != 1) {
            //Le tableau de bord n'est pas dans un seul écran.
            return false;
        }
        Screen tabBordScreen = screensContainingTabBord.get(0);

        //Check that screen is big enough
        return !(tabBordScreen != null && (tabBordScreen.getBounds().getWidth() < 800 || tabBordScreen.getBounds().getHeight() < 600));
    }

    public void sendMaximizeEvent(Stage stage) {
        if (!isEnvironmentRight(stage)) throw new IllegalStateException();
        Screen mainScreen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0);

        //Determine which screen has tabBord and which has vis.
        ObservableList<Screen> screens = Screen.getScreens();
        int numScreenTabBord = screens.indexOf(mainScreen);
        int numScreenVis = numScreenTabBord == 0 ? 1 : 0;

        Consumer<Stage> consumerForTabBord = stage1 -> {
            setupToScreen.accept(stage1, numScreenTabBord);
        };
        Consumer<Stage> consumerForVis = stage1 -> {
            setupToScreen.accept(stage1, numScreenVis);
        };

        eventBus.post(new ScreenDispatchEvent(consumerForTabBord, consumerForVis));
    }

    public void sendRestoreEvent(Stage stage) {
        Consumer<Stage> consumerForTabBord = stage1 -> {
            //Do nothing.
        };
        Consumer<Stage> consumerForVis = stage1 -> {
            stage1.setWidth(800);
            stage1.setHeight(600);
        };

        eventBus.post(new ScreenDispatchEvent(consumerForTabBord, consumerForVis));
    }
}
