package edu.iti.javaii.project;

import iWishManger.IWishManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;
    private static IWishManager wishManager;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        wishManager = new IWishManager();

        wishManager.start(primaryStage);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    public static void main(String[] args) {
        launch(App.class, args);
    }
}
