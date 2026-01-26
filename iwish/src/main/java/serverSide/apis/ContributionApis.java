package serverSide.apis;

import dtos.Notification; 
import dtos.requestDtos.contributionHandler.AddContributionRequest;
import dtos.requestDtos.contributionHandler.RemoveContributionRequest;
import serverSide.NotificationManger;
import serverSide.dbLayer.ContributionHandler;
import serverSide.dbLayer.NotificationHandler;

public class ContributionApis {

    private final ContributionHandler contributionHandler;
    private final WishListApis wishListApis;
    private final UserApis userApis;

    private final NotificationHandler notificationHandler;

    public ContributionApis(ContributionHandler contributionHandler,
                            WishListApis wishListApis,
                            UserApis userApis,
                            NotificationHandler notificationHandler) {
        this.contributionHandler = contributionHandler;
        this.wishListApis = wishListApis;
        this.userApis = userApis;
        this.notificationHandler = notificationHandler;
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

        double totalAmount = wishListApis.getWishListTotalAmount(r.getWishListId());
        String contributorName = userApis.getUserNameById(r.getUserId());

        String title = "New Contribution 🎁";
        String body =
            contributorName + " contributed " + r.getAmount() + " to your wishlist.\n" +
            "Total amount: " + totalAmount + "\n" +
            "Remaining amount: " + (totalAmount - r.getAmount());

        models.Notification saved = notificationHandler.addNotification(ownerUserId, title, body);
        try {
            Notification realtime = new Notification(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(ownerUserId, realtime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    public Object removeContribution(RemoveContributionRequest r) {
        return contributionHandler.removeContribution(r.getContributionId(), r.getUserId(), r.getWishListId());
    }
}
