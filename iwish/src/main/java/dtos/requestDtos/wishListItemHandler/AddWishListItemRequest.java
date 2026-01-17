package dtos.requestDtos.wishListItemHandler;


import dtos.Request;

public class AddWishListItemRequest implements Request {
    private final int wishListId;
    private final int itemId;

    public AddWishListItemRequest(int wishListId, int itemId) {
        this.wishListId = wishListId;
        this.itemId = itemId;
    }

    public int getWishListId() { return wishListId; }
    public int getItemId() { return itemId; }
}
