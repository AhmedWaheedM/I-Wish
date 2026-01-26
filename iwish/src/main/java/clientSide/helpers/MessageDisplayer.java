package clientSide.helpers;

import dtos.Notification;
import javafx.scene.control.Alert;

public class MessageDisplayer {

    public static void showError(String message , String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    public static void showSuccess(String message , String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void showNotification(Notification notification) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(notification.getTitle());
        alert.setHeaderText("Notification Received");
        alert.setContentText(notification.getBody());
        alert.show();
    }


}
