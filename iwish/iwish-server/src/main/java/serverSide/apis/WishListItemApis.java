package serverSide.apis;

import java.util.List;

import dtos.requestDtos.userHandler.UpdateBalanceRequest;
import dtos.requestDtos.wishListHandler.UpdateWishListCurrentAmountRequest;
import dtos.requestDtos.wishListItemHandler.AddWishListItemRequest;
import dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest;
import dtos.requestDtos.wishListItemHandler.RemoveWishListItemRequest;
import serverSide.dbLayer.WishListItemHandler;
import serverSide.services.NotificationService;

public class WishListItemApis {

    private final WishListItemHandler wishListItemHandler;
    private final ContributionApis contributionApis;
    private final NotificationService notificationService;
    private final WishListApis wishListApis;
    private final UserApis userApis;

    public WishListItemApis(WishListItemHandler wishListItemHandler, ContributionApis contributionApis, NotificationService notificationService , WishListApis wishListApis ,UserApis userApis) {
        this.wishListApis = wishListApis;
        this.wishListItemHandler = wishListItemHandler;
        this.contributionApis = contributionApis;
        this.notificationService = notificationService;
        this.userApis = userApis;
    }

    public Object addWishListItem(AddWishListItemRequest r) {
        wishListItemHandler.addWishListItem(r.getWishListId(), r.getItemId());
        return true;
    }

    public Object removeWishListItem(RemoveWishListItemRequest r) {

        int itemId = r.getItemId();
        Integer wishListId = r.getWishListId();

        int wishListItemRecId = wishListItemHandler.getRecIdByWishListAndItem(r.getWishListId(), r.getItemId());

        if (wishListId == null) return false;

        List<Integer> contributorIds = contributionApis.getContributorUserIdsForItem(wishListItemRecId);

        for (Integer userId : new java.util.HashSet<>(contributorIds)) {

            double amount = contributionApis.getUserContributionToItem(userId, wishListItemRecId);
            if (amount <= 0) continue;

            contributionApis.removeUserContributionsForItem(userId, wishListItemRecId);

            notificationService.notifyUser(
                userId,
                "Contribution Refunded 💸",
                "The item you contributed " + amount + " to was removed from the wishlist. Your contribution has been refunded."
            );

            UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest(
                userId,
                amount,
                '+'
            );
            userApis.updateBalance(updateBalanceRequest);

            wishListApis.updateCurrentAmount(new UpdateWishListCurrentAmountRequest(
                wishListId,
                amount,
                '-'
            ));
        }

        wishListItemHandler.removeWishListItem(wishListId, itemId);

        return true;
    }


    public Object getWishListItems(GetWishListItemsRequest r) {
        return wishListItemHandler.getWishListItems(r.getWishListId());
    }
}