package dtos.responseDtos;

import dtos.Response;

public class NotificationResponse implements Response {
    private String type; // e.g., "GIFT_COMPLETED", "NEW_CONTRIBUTION"
    private String message;

    public NotificationResponse(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
