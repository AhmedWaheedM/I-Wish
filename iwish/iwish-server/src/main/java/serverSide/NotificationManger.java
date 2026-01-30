package serverSide;

import models.Notification;

public class NotificationManger {

    public static void sendNotificaiton(int userId , Notification notification){
        ClientHandler clientHandler = OnlineUserTracker.onlineUsers.get(userId);
        

        if (clientHandler != null){
            clientHandler.sendNotification(notification);
        }
    }

}
