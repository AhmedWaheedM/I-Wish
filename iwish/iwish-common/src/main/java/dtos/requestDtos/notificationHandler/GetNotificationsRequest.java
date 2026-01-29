package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class GetNotificationsRequest implements Request {

    private final int userId;

    public GetNotificationsRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
