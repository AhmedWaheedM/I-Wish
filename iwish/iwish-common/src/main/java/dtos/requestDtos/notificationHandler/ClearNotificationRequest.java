package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class ClearNotificationRequest implements Request {

    private final int notificationId;

    public ClearNotificationRequest(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
