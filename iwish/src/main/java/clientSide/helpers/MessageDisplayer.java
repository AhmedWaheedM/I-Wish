package clientSide.helpers;

import dtos.Notification;
import javafx.scene.control.Alert;

public class MessageDisplayer {

    public static void showError(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void showSuccess(String message, String title) {
        // Add to right sidebar notification list
        NotificationService.getInstance().addNotification(
            title,
            message,
            NotificationService.NotificationType.INFO
        );
    }

    public static void showContributionSuccess(String itemName, double amount) {
        String message = String.format("Contribution of $%.2f to %s successful!", amount, itemName);
        NotificationService.getInstance().addNotification(
            "Contribution Success",
            message,
            NotificationService.NotificationType.CONTRIBUTION
        );
    }

    public static void showFundingMilestone(String itemName, int percentage) {
        NotificationService.getInstance().showFundingMilestoneNotification(itemName, percentage);
    }

    public static void showNotification(Notification notification) {
        NotificationService.getInstance().addNotification(
            notification.getTitle(),
            notification.getBody(),
            NotificationService.NotificationType.INFO
        );
    }
}
