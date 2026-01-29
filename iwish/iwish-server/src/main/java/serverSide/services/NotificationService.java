package serverSide.services;

import dtos.NotificationDto;
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
            NotificationDto realtime = new NotificationDto(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(userId, realtime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
