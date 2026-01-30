package serverSide;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ServerController {

    @FXML
    private Label statusLabel;
    
    @FXML
    private Button startBtn;
    
    @FXML
    private Button stopBtn;

    private Server server;

    public void initialize() {
        server = new Server();
    }

    @FXML
    private void handleStart() {
        server.startServer();
        updateUI(true);
    }

    @FXML
    private void handleStop() {
        server.stopServer();
        updateUI(false);
    }

    private void updateUI(boolean isRunning) {
        if (isRunning) {
            statusLabel.setText("Running");
            statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: green; -fx-font-weight: bold;");
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
        } else {
            statusLabel.setText("Stopped");
            statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");
            startBtn.setDisable(false);
            stopBtn.setDisable(true);
        }
    }
}