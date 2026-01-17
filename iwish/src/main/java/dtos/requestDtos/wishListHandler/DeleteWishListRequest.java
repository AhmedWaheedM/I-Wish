package dtos.requestDtos.wishListHandler;


import dtos.Request;

public class DeleteWishListRequest implements Request {
    private final int wishListId;
    public DeleteWishListRequest(int wishListId) { this.wishListId = wishListId; }
    public int getWishListId() { return wishListId; }
}
