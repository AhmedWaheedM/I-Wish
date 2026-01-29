package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class GetUnreadNotificationsRequest implements Request {

    private final int userId;

    public GetUnreadNotificationsRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
