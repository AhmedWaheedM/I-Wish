package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class ClearAllNotificationsRequest implements Request {

    private final int userId;

    public ClearAllNotificationsRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
