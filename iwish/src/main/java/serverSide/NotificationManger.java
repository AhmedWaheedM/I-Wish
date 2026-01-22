package serverSide;

import dtos.Notification;

public class NotificationManger {

    public static void sendNotificaiton(int userId , Notification notification){
        ClientHandler clientHandler = OnlineUserTracker.onlineUsers.get(userId);
        clientHandler.sendNotification(notification);
    }

}
