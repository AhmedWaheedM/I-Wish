package dtos.responseDtos.wishListHandler;

import dtos.Response;
import models.WishList;
import java.util.List;

public class GetFriendsWishListsResponse implements Response {
    private List<WishList> friendsWishLists;

    public GetFriendsWishListsResponse(List<WishList> friendsWishLists) {
        this.friendsWishLists = friendsWishLists;
    }

    public List<WishList> getFriendsWishLists() { return friendsWishLists; }
    public void setFriendsWishLists(List<WishList> friendsWishLists) { this.friendsWishLists = friendsWishLists; }
}
