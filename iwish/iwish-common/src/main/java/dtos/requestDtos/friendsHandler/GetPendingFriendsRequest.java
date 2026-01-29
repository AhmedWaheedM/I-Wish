package dtos.requestDtos.friendsHandler;

import dtos.Request;

public class GetPendingFriendsRequest implements Request {
    private final int userId;
    public GetPendingFriendsRequest(int userId) { this.userId = userId; }
    public int getUserId() { return userId; }
}
