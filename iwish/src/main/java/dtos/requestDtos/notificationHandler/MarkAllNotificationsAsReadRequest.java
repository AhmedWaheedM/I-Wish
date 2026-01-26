package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class MarkAllNotificationsAsReadRequest implements Request {

    private final int userId;

    public MarkAllNotificationsAsReadRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
