package clientSide.controllers;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class NotificationCellController {

    @FXML private Label titleLabel;
    @FXML private Label bodyLabel;
    @FXML private Label unreadBadge;
    @FXML private Button markReadBtn;

    private NotificationsController.AppNotification notification;
    private Consumer<NotificationsController.AppNotification> onMarkRead;

    public void setData(NotificationsController.AppNotification notification,
                        Consumer<NotificationsController.AppNotification> onMarkRead) {
        this.notification = notification;
        this.onMarkRead = onMarkRead;

        titleLabel.setText(notification.getTitle());
        bodyLabel.setText(notification.getBody());

        boolean isUnread = !notification.isRead();
        unreadBadge.setVisible(isUnread);
        unreadBadge.setManaged(isUnread);

        markReadBtn.setVisible(isUnread);
        markReadBtn.setManaged(isUnread);

        
    }

    @FXML
    private void onMarkRead() {
        if (notification == null || onMarkRead == null) return;
        onMarkRead.accept(notification);
    }
}
