package clientSide.views;

import dtos.Notification;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class NotificationPanel {

    @FXML
    private Label notificationLabel;

    public void show(Notification n) {
        if (notificationLabel != null) {
            notificationLabel.setText(n.getTitle() + " : " + n.getBody());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(n.getTitle());
        alert.setHeaderText(null);
        alert.setContentText(n.getBody());
        alert.show();
    }
}
