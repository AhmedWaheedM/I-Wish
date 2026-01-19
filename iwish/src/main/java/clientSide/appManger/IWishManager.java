package clientSide.appManger;

import clientSide.ClientConnection;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;

public class IWishManager {

    private static Stage stage;
    private static ClientConnection client;
    private static User loggedInUser;
    // 🔹 Initialize once from Main
    public IWishManager(Stage primaryStage) {
        stage = primaryStage;

        try {
            client = new ClientConnection();
            client.connect("127.0.0.1", 5005);
            System.out.println("Connected to server");

            switchScene("login", "iWish - Login");

        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchScene(String sceneName, String title) {
        try {
            String fxmlPath = "/views/" + sceneName + "/" + sceneName + ".fxml";
            var url = IWishManager.class.getResource(fxmlPath);

            if (url == null) {
                throw new RuntimeException("FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (stage.getScene() == null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
            } else {
                stage.getScene().setRoot(root);
            }

            stage.getScene().getStylesheets().clear();
            String cssPath = "/views/" + sceneName + "/" + sceneName + ".css";
            var cssUrl = IWishManager.class.getResource(cssPath);
            if (cssUrl != null) {
                stage.getScene().getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        try {
            if (client != null) client.close();
        } catch (Exception ignored) {}

        Platform.exit();
    }

    public static ClientConnection getClient() {
        return client;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }
}
