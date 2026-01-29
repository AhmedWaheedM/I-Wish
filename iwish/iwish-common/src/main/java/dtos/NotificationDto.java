package dtos;

public class NotificationDto implements Request {

    private final String title;
    private final String body;

    public NotificationDto(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
