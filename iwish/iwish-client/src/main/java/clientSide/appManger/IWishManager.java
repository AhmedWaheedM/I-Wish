package clientSide.appManger;

import clientSide.ClientConnection;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;

public class IWishManager {

    private static Stage stage;
    private static ClientConnection client;
    private static User loggedInUser;
    private static javafx.scene.layout.StackPane rootStack;
    private static javafx.scene.layout.VBox toastHost;

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
            if (url == null) throw new RuntimeException("FXML not found: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(url);
            Parent pageRoot = loader.load();

            if (rootStack == null) {
                rootStack = new StackPane();
                toastHost = new VBox(10);
                toastHost.setPickOnBounds(false); 
                toastHost.setMouseTransparent(false);

                StackPane.setAlignment(toastHost, Pos.TOP_RIGHT);
                StackPane.setMargin(toastHost, new Insets(20));

                rootStack.getChildren().addAll(pageRoot, toastHost);

                Scene scene = new Scene(rootStack);
                stage.setScene(scene);
                stage.setMaximized(true);

                // IMPORTANT: let ToastManager access the host
                clientSide.appManger.ToastManager.init(toastHost);

            } else {
                // Replace page root but keep toastHost
                rootStack.getChildren().set(0, pageRoot);
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

    public static void login(User user) {
        setLoggedInUser(user);
    }

    public static void logout() {
        loggedInUser = null;
    }
}
