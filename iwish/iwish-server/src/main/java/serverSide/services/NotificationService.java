package serverSide.services;

import models.Notification;
import serverSide.NotificationManger;
import serverSide.dbLayer.NotificationHandler;

public class NotificationService {

    private final NotificationHandler notificationHandler;

    public NotificationService(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    public void notifyUser(int userId, String title, String body) {
            System.out.println("Creating notification for user " + userId + ": " + title + " - " + body);
        models.Notification saved = notificationHandler.addNotification(userId, title, body);

        try {
            System.out.println("Notification saved: " + saved.getTitle() + " - " + saved.getBody());
            Notification realtime = new Notification(saved.getUserId(), saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(userId, realtime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
