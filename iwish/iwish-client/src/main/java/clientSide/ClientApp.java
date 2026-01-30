package clientSide;

import clientSide.appManger.IWishManager;
import javafx.application.Application;

public class ClientApp extends Application {
    private IWishManager manager;
    private static ClientConnection clientConnection;

    public static ClientConnection getClientConnection() {
        return clientConnection;
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) {
        try {
            clientConnection = new ClientConnection();
            clientConnection.connect("localhost", 5005);
            System.out.println("Connected to server.");
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            // e.printStackTrace(); 
            // Continue starting UI even if connection fails for now, or show error?
            // For dev flow, continuing is often better to at least see the UI.
        }
        manager = new IWishManager(primaryStage);
    }

    @Override
    public void stop() {
        if (manager != null) {
            manager.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
