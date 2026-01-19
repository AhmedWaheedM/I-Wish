package dtos.responseDtos.contributionHandler;

import dtos.Response;

public class ContributionResponse implements Response {
    private boolean success;
    private String message;
    private boolean itemCompleted; 

    public ContributionResponse(boolean success, String message, boolean itemCompleted) {
        this.success = success;
        this.message = message;
        this.itemCompleted = itemCompleted;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isItemCompleted() { return itemCompleted; }
    public void setItemCompleted(boolean itemCompleted) { this.itemCompleted = itemCompleted; }
}
