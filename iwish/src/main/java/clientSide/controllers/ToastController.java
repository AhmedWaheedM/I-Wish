package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ToastController {

    @FXML private Label titleLabel;
    @FXML private Label bodyLabel;

    public void setContent(String title, String body) {
        titleLabel.setText(title);
        bodyLabel.setText(body);
    }
}
