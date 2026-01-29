package dtos;

import java.io.Serializable;

public class ServerShutdownNotification implements Request { 
    // Just a marker class, no implementation needed for simple message
    private String message = "Server is shutting down.";

    public String getMessage() {
        return message;
    }
}
