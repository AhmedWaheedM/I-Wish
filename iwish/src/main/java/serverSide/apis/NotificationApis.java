package serverSide.apis;

import dtos.Notification; // (your DTO used for real-time push)
import dtos.requestDtos.notificationHandler.AddNotificationRequest;
import dtos.requestDtos.notificationHandler.ClearAllNotificationsRequest;
import dtos.requestDtos.notificationHandler.ClearNotificationRequest;
import dtos.requestDtos.notificationHandler.GetNotificationsRequest;
import dtos.requestDtos.notificationHandler.GetUnreadNotificationsRequest;
import dtos.requestDtos.notificationHandler.MarkAllNotificationsAsReadRequest;
import dtos.requestDtos.notificationHandler.MarkNotificationAsReadRequest;
import serverSide.NotificationManger;
import serverSide.dbLayer.NotificationHandler;

public class NotificationApis {

    private final NotificationHandler notificationHandler;

    public NotificationApis(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    public Object addNotification(AddNotificationRequest r) {
        notificationHandler.addNotification(r.getUserId(), r.getTitle(), r.getBody());

        Notification n = new Notification(r.getTitle(), r.getBody());
        NotificationManger.sendNotificaiton(r.getUserId(), n);

        return true;
    }

    public Object getNotifications(GetNotificationsRequest r) {
        return notificationHandler.getNotificationsByUserId(r.getUserId());
    }

    public Object getUnread(GetUnreadNotificationsRequest r) {
        return notificationHandler.getUnreadNotificationsByUserId(r.getUserId());
    }

    public Object markOneRead(MarkNotificationAsReadRequest r) {
        return notificationHandler.markAsRead(r.getNotificationId());
    }

    public Object markAllRead(MarkAllNotificationsAsReadRequest r) {
        return notificationHandler.markAllAsRead(r.getUserId());
    }

    public Object clearNotification(ClearNotificationRequest r) {
        return notificationHandler.clearNotification(r.getNotificationId());
    }

    public Object clearAllNotifications(ClearAllNotificationsRequest r) {
        return notificationHandler.clearAllNotifications(r.getUserId());
    }
}
