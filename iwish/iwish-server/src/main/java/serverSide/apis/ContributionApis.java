package serverSide.apis;

import java.util.HashSet;
import java.util.Set;

import dtos.requestDtos.contributionHandler.AddContributionRequest;
import dtos.requestDtos.contributionHandler.RemoveContributionRequest;
import serverSide.dbLayer.ContributionHandler;
import serverSide.services.NotificationService;

public class ContributionApis {

    private final ContributionHandler contributionHandler;
    private final WishListApis wishListApis;
    private final UserApis userApis;
    private final NotificationService notificationService;

    public ContributionApis(ContributionHandler contributionHandler,
                            WishListApis wishListApis,
                            UserApis userApis,
                            NotificationService notificationService) {
        this.contributionHandler = contributionHandler;
        this.wishListApis = wishListApis;
        this.userApis = userApis;
        this.notificationService = notificationService;
    }

    public Object addContribution(AddContributionRequest r) {

        boolean ok = contributionHandler.addContribution(
            r.getUserId(),
            r.getWishListId(),
            r.getWishListItemId(),
            r.getAmount()
        );

        if (!ok) return false;

        Integer ownerUserId = wishListApis.getUserIdByWishListId(r.getWishListId());
        if (ownerUserId == null) return true;

        String contributorName = userApis.getUserNameById(r.getUserId());
        String ownerName = userApis.getUserNameById(ownerUserId);

        double wishListTotal = wishListApis.getWishListTotalAmount(r.getWishListId());
        double wishListCurrent = wishListApis.getWishListCurrentAmount(r.getWishListId());
        double wishListRemaining = Math.max(0, wishListTotal - wishListCurrent);

        wishListTotal = Math.round(wishListTotal * 100.0) / 100.0;
        wishListCurrent = Math.round(wishListCurrent * 100.0) / 100.0;
        wishListRemaining = Math.round(wishListRemaining * 100.0) / 100.0;


        boolean fullyFunded = contributionHandler.isItemFullyFunded(r.getWishListItemId());

        String title;
        String body;

        if (fullyFunded) {
            title = "Item Fully Funded! 🎉";
            body =
                "An item in your wishlist is now fully funded.\n" +
                "Last contribution by: " + contributorName + " (" + r.getAmount() + ")\n" +
                "Wishlist funded: " + wishListCurrent + " / " + wishListTotal + "\n" +
                "Wishlist remaining: " + wishListRemaining;

            notificationService.notifyUser(ownerUserId, title, body);

            notifyAllContributorsForItem(
                r.getWishListItemId(),
                ownerUserId,
                title,
                "Thanks! An item you contributed to has been fully funded 🎉\n" +
                "Wishlist owner: " + ownerName
            );

        } else {
            title = "New Contribution 🎁";
            body =
                contributorName + " contributed " + r.getAmount() + " to your wishlist.\n" +
                "Wishlist funded: " + wishListCurrent + " / " + wishListTotal + "\n" +
                "Wishlist remaining: " + wishListRemaining;

            notificationService.notifyUser(ownerUserId, title, body);
        }

        return true;
    }

    public Object removeContribution(RemoveContributionRequest r) {
        return contributionHandler.removeContribution(
            r.getContributionId(),
            r.getUserId(),
            r.getWishListId()
        );
    }
    public void removeUserContributionsForItem(int userId, int wishListItemRecId) {
        contributionHandler.removeUserContributionsForItem(userId, wishListItemRecId);
    }

    public double getUserContributionToItem(int userId, int wishListItemRecId) {
        return contributionHandler.getUserContributionToItem(userId, wishListItemRecId);
    }
    public java.util.List<Integer> getContributorUserIdsForItem(int wishListItemRecId) {
        return contributionHandler.getContributorUserIdsForItem(wishListItemRecId);
    }
    private void notifyAllContributorsForItem(int wishListItemRecId, Integer excludeUserId, String title, String body) {

        var contributorIds = contributionHandler.getContributorUserIdsForItem(wishListItemRecId);

        Set<Integer> unique = new HashSet<>(contributorIds);

        if (excludeUserId != null) unique.remove(excludeUserId);

        for (Integer userId : unique) {
            notificationService.notifyUser(userId, title, body);
        }
    }
}