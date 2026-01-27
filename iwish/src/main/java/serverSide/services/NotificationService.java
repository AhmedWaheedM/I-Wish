package serverSide.services;

import dtos.Notification;
import serverSide.NotificationManger;
import serverSide.dbLayer.NotificationHandler;

public class NotificationService {

    private final NotificationHandler notificationHandler;

    public NotificationService(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    public void notifyUser(int userId, String title, String body) {
        models.Notification saved = notificationHandler.addNotification(userId, title, body);

        try {
            Notification realtime = new Notification(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(userId, realtime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
