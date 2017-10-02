package server;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:18
 */
import com.google.inject.Provider;

public class WebsocketHandlerImplProvider implements Provider<WebsocketHandlerImpl> {
    public WebsocketHandlerImpl get() {
return new WebsocketHandlerImpl();
    }
}
