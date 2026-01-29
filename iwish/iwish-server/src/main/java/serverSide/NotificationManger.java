package serverSide;

import dtos.NotificationDto;

public class NotificationManger {

    public static void sendNotificaiton(int userId , NotificationDto notification){
        ClientHandler clientHandler = OnlineUserTracker.onlineUsers.get(userId);
        

        if (clientHandler != null){
            clientHandler.sendNotification(notification);
        }
    }

}
