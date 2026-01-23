package dtos.requestDtos.friendsHandler;

import dtos.Request;

public class GetNonFriendsRequest implements Request {
    private final int userId;
    public GetNonFriendsRequest(int userId) { this.userId = userId; }
    public int getUserId() { return userId; }
}
