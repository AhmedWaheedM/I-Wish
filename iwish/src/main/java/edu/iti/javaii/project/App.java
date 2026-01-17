package edu.iti.javaii.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try {
            // CHANGE THIS to your login fxml name
            // Example: "login.fxml" or "dashboard.fxml"
            FXMLLoader loader = new FXMLLoader(App.class.getResource("dashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 640, 480);
            stage.setScene(scene);
            stage.setTitle("iWish - Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlName));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
