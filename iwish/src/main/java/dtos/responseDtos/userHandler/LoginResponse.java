package dtos.responseDtos.userHandler;

import dtos.Response;
import models.User;

public class LoginResponse implements Response {
    private User user;
    private String errorMessage;
    private boolean success;

    public LoginResponse(User user, String errorMessage, boolean success) {
        this.user = user;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
