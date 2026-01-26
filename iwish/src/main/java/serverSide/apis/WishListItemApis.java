package serverSide.apis;

import dtos.requestDtos.wishListItemHandler.AddWishListItemRequest;
import dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest;
import dtos.requestDtos.wishListItemHandler.RemoveWishListItemRequest;
import serverSide.dbLayer.WishListItemHandler;

public class WishListItemApis {

    private final WishListItemHandler wishListItemHandler;

    public WishListItemApis(WishListItemHandler wishListItemHandler) {
        this.wishListItemHandler = wishListItemHandler;
    }

    public Object addWishListItem(AddWishListItemRequest r) {
        wishListItemHandler.addWishListItem(r.getWishListId(), r.getItemId());
        return true;
    }

    public Object removeWishListItem(RemoveWishListItemRequest r) {
        wishListItemHandler.removeWishListItem(r.getWishListId(), r.getItemId());
        return true;
    }

    public Object getWishListItems(GetWishListItemsRequest r) {
        return wishListItemHandler.getWishListItems(r.getWishListId());
    }
}
