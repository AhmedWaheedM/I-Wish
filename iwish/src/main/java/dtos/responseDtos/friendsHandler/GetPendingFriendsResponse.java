package dtos.responseDtos.friendsHandler;

import dtos.Response;
import models.User;
import java.util.List;

public class GetPendingFriendsResponse implements Response {
    private boolean success;
    private List<User> pendingFriends;
    private String errorMessage;

    public GetPendingFriendsResponse(boolean success, List<User> pendingFriends, String errorMessage) {
        this.success = success;
        this.pendingFriends = pendingFriends;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<User> getPendingFriends() { return pendingFriends; }
    public void setPendingFriends(List<User> pendingFriends) { this.pendingFriends = pendingFriends; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
