package dtos.requestDtos.wishListItemHandler;

import dtos.Request;

public class GetWishListItemsRequest implements Request {
    private int wishListId;

    public GetWishListItemsRequest(int wishListId) {
        this.wishListId = wishListId;
    }

    public int getWishListId() {
        return wishListId;
    }
}
