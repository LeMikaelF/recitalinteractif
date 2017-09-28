package server;

import static spark.Spark.*;

public class Server {
    private final static String STATIC_FILES_LOCATION = "/";

    public static void startServer() {
        port(80);
        staticFileLocation("/public");
        webSocket("/ws", server.SocketQueueHandler.class);
        init();
    }

    public static void main(String[] args) throws Exception {
        startServer();
    }

    public static void stopServer() {
        stop();
    }
}