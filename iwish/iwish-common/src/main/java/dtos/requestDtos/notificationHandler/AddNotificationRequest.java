package dtos.requestDtos.notificationHandler;

import dtos.Request;

public class AddNotificationRequest implements Request {

    private final int userId;
    private final String title;
    private final String body;

    public AddNotificationRequest(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
