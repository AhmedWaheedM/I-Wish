package serverSide.apis;

import dtos.requestDtos.wishListHandler.DeleteWishListRequest;
import dtos.requestDtos.wishListHandler.GetFriendsWishListsRequest;
import dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest;
import dtos.requestDtos.wishListHandler.UpdateWishListCurrentAmountRequest;
import serverSide.dbLayer.WishListHandler;

public class WishListApis {

    private final WishListHandler wishListHandler;

    public WishListApis(WishListHandler wishListHandler) {
        this.wishListHandler = wishListHandler;
    }

    public Object getWishListByUserId(GetWishListByUserIdRequest r) {
        return wishListHandler.getWishListByUserId(r.getUserId());
    }

    public Object getFriendsWishLists(GetFriendsWishListsRequest r) {
        return wishListHandler.getFriendsWishLists(r.getUserId());
    }

    public Object updateCurrentAmount(UpdateWishListCurrentAmountRequest r) {
        return wishListHandler.updateWishListCurrentAmount(r.getWishListId(), r.getAmount(), r.getOperation());
    }

    public Object deleteWishList(DeleteWishListRequest r) {
        return wishListHandler.deleteWishList(r.getWishListId());
    }

    public Integer getUserIdByWishListId(int wishListId) {
        return wishListHandler.getUserIdByWishListId(wishListId);
    }

    public double getWishListTotalAmount(int wishListId) {
        Double total = wishListHandler.getWishListTotalAmount(wishListId);
        return total == null ? 0.0 : total;
    }

    public double getWishListCurrentAmount(int wishListId) {
        Double current = wishListHandler.getWishListCurrentAmount(wishListId);
        return current == null ? 0.0 : current;
    }

    
}
