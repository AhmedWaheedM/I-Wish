package dtos.responseDtos.friendsHandler;

import dtos.Response;
import models.User;
import java.util.List;

public class GetFriendsResponse implements Response {
    private boolean success;
    private List<User> friends;
    private String errorMessage;

    public GetFriendsResponse(boolean success, List<User> friends, String errorMessage) {
        this.success = success;
        this.friends = friends;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<User> getFriends() { return friends; }
    public void setFriends(List<User> friends) { this.friends = friends; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
