package events;

import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * Created by Mikaël on 2017-10-09.
 */
public class ScreenDispatchEvent {

    private final Consumer<Stage> ifYouAreTabBordRunThis;
    private final Consumer<Stage> ifYouAreVisRunThis;

    public ScreenDispatchEvent(Consumer<Stage> ifYouAreTabBordRunThis, Consumer<Stage> ifYouAreVisRunThis) {
        this.ifYouAreTabBordRunThis = ifYouAreTabBordRunThis;
        this.ifYouAreVisRunThis = ifYouAreVisRunThis;
    }

    public Consumer<Stage> ifYouAreTabBordRunThis() {
        return ifYouAreTabBordRunThis;
    }

    public Consumer<Stage> ifYouAreVisRunThis() {
        return ifYouAreVisRunThis;
    }
}
