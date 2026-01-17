package dtos.requestDtos.wishListHandler;


import dtos.Request;

public class GetFriendsWishListsRequest implements Request {
    private final int userId;
    public GetFriendsWishListsRequest(int userId) { this.userId = userId; }
    public int getUserId() { return userId; }
}
