package dtos.requestDtos.wishListHandler;


import dtos.Request;

public class GetWishListByUserIdRequest implements Request {
    private final int userId;
    public GetWishListByUserIdRequest(int userId) { this.userId = userId; }
    public int getUserId() { return userId; }
}
