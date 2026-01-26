package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Notification;

public class NotificationHandler extends DBHandler {

    public NotificationHandler() {
        super("notification");
    }
    public Notification addNotification(int userId, String title, String body) {
        String query = "INSERT INTO " + tableName + " (user_id, title, body, is_read) VALUES (?, ?, ?, ?)";
        Notification notification = new Notification(userId, title, body);

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, body);
            pstmt.setBoolean(4, false);

            pstmt.executeUpdate();

            resultSet = pstmt.getGeneratedKeys();
            if (resultSet.next()) {
                notification.setNotificationId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return notification;
    }

    public Notification getNotificationById(int notificationId) {
        String query = "SELECT * FROM " + tableName + " WHERE notification_id = ?";
        Notification notification = null;

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, notificationId);

            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                notification = mapRowToNotification();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return notification;
    }

    public List<Notification> getNotificationsByUserId(int userId) {
        String query = "SELECT * FROM " + tableName + " WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                notifications.add(mapRowToNotification());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return notifications;
    }

    public List<Notification> getUnreadNotificationsByUserId(int userId) {
        String query = "SELECT * FROM " + tableName + " WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                notifications.add(mapRowToNotification());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return notifications;
    }

    public int countUnreadByUserId(int userId) {
        String query = "SELECT COUNT(*) AS cnt FROM " + tableName + " WHERE user_id = ? AND is_read = FALSE";
        int count = 0;

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);

            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return count;
    }

    public boolean markAsRead(int notificationId) {
        String query = "UPDATE " + tableName + " SET is_read = TRUE WHERE notification_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
        return true;
    }

    public boolean markAllAsRead(int userId) {
        String query = "UPDATE " + tableName + " SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
        return true;
    }


    private Notification mapRowToNotification() throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(resultSet.getInt("notification_id"));
        n.setUserId(resultSet.getInt("user_id"));
        n.setTitle(resultSet.getString("title"));
        n.setBody(resultSet.getString("body"));
        n.setRead(resultSet.getBoolean("is_read"));

        try {
            n.setCreatedAt(resultSet.getTimestamp("created_at"));
        } catch (SQLException ignored) {}

        return n;
    }
}
