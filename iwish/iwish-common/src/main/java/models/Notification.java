package models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Notification implements Serializable {
    private int notificationId;
    private int userId;
    private String title;
    private String body;
    private boolean isRead;
    private boolean cleared;
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.isRead = false;
        this.cleared = false;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isCleared() { return cleared; }
    public void setCleared(boolean cleared) { this.cleared = cleared; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
