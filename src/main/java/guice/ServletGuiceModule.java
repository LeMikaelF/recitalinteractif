package guice;

import com.google.inject.servlet.ServletModule;
import server.MyWebSocketServlet;

/**
 * Created by Mikaël on 2017-10-02.
 */
public class ServletGuiceModule extends ServletModule {
    @Override
    protected void configureServlets(){
        serve("/ws").with(MyWebSocketServlet.class);
    }
}
