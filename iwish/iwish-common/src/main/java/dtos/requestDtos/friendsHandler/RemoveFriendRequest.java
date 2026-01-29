package dtos.requestDtos.friendsHandler;

import dtos.Request;

public class RemoveFriendRequest implements Request {
    private final int user1Id;
    private final int user2Id;

    public RemoveFriendRequest(int user1Id, int user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public int getUser1Id() { return user1Id; }
    public int getUser2Id() { return user2Id; }
}
