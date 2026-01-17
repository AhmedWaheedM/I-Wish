package dtos.requestDtos.friendsHandler;

import dtos.Request;

public class GetFriendsRequest implements Request {
    private final int userId;
    public GetFriendsRequest(int userId) { this.userId = userId; }
    public int getUserId() { return userId; }
}