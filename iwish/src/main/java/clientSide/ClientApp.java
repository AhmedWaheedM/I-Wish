package clientSide;

import clientSide.appManger.IWishManager;
import javafx.application.Application;

public class ClientApp extends Application {
    private IWishManager manager;

    @Override
    public void start(javafx.stage.Stage primaryStage) {
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
