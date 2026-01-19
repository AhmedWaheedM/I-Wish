package dtos.responseDtos.userHandler;

import dtos.Response;

public class RegisterResponse implements Response {
    private boolean success;
    private String errorMessage;

    public RegisterResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
