package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class MarkNotificationAsReadRequest implements Request {

    private final int notificationId;

    public MarkNotificationAsReadRequest(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
