package server;

import static spark.Spark.*;

public class Server {
    private final static String STATIC_FILES_LOCATION = "/";

    public static void startServer() {
        Runnable runnable = () -> {
            port(80);
            staticFileLocation("/public");
            webSocket("/ws", WebsocketHandler.class);
            init();
            WebsocketHandler.startBroadcasting();
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }

    public static void main(String[] args) throws Exception {
        startServer();
    }

    public static void stopServer() {
        stop();
    }
}