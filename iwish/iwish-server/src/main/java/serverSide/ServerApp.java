package serverSide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/server_ui.fxml"));
        primaryStage.setTitle("I-Wish Server");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
        
        // Ensure server stops when window closes
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}