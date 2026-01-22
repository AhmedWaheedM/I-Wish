package serverSide;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import dtos.NotificationDTO;

public class SessionManager {
    private static final Map<Integer, ClientHandler> activeUsers = new ConcurrentHashMap<>();

    public static void registerUser(int userId, ClientHandler handler) {
        activeUsers.put(userId, handler);
    }

    public static void unregisterUser(int userId) {
        activeUsers.remove(userId);
    }

    public static void sendNotification(int userId, NotificationDTO notification) {
        ClientHandler handler = activeUsers.get(userId);
        if (handler != null) {
            handler.pushMessage(notification);
        }
    }
}