package serverSide;

import dtos.Request;
import dtos.requestDtos.Item.AddItemRequest;
import dtos.requestDtos.Item.DeleteItemRequest;
import dtos.requestDtos.Item.GetAllItemsRequest;
import dtos.requestDtos.Item.GetItemByIdRequest;
import dtos.requestDtos.Item.GetItemPriceRequest;
import dtos.requestDtos.contributionHandler.AddContributionRequest;
import dtos.requestDtos.contributionHandler.RemoveContributionRequest;
import dtos.requestDtos.friendsHandler.AcceptFriendRequest;
import dtos.requestDtos.friendsHandler.AddFriendRequest;
import dtos.requestDtos.friendsHandler.GetFriendsRequest;
import dtos.requestDtos.friendsHandler.GetNonFriendsRequest;
import dtos.requestDtos.friendsHandler.GetPendingFriendsRequest;
import dtos.requestDtos.friendsHandler.RejectFriendRequest;
import dtos.requestDtos.friendsHandler.RemoveFriendRequest;
import dtos.requestDtos.notificationHandler.AddNotificationRequest;
import dtos.requestDtos.notificationHandler.GetNotificationsRequest;
import dtos.requestDtos.notificationHandler.GetUnreadNotificationsRequest;
import dtos.requestDtos.notificationHandler.MarkAllNotificationsAsReadRequest;
import dtos.requestDtos.notificationHandler.MarkNotificationAsReadRequest;
import dtos.requestDtos.userHandler.HasEnoughBalanceRequest;
import dtos.requestDtos.userHandler.LoginRequest;
import dtos.requestDtos.userHandler.RegisterationRequest;
import dtos.requestDtos.userHandler.UpdateBalanceRequest;
import dtos.requestDtos.wishListHandler.DeleteWishListRequest;
import dtos.requestDtos.wishListHandler.GetFriendsWishListsRequest;
import dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest;
import dtos.requestDtos.wishListHandler.UpdateWishListCurrentAmountRequest;
import dtos.requestDtos.wishListItemHandler.AddWishListItemRequest;
import dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest;
import dtos.requestDtos.wishListItemHandler.RemoveWishListItemRequest;
import models.User;
import serverSide.apis.ContributionApis;
import serverSide.apis.FriendsApis;
import serverSide.apis.ItemApis;
import serverSide.apis.NotificationApis;
import serverSide.apis.UserApis;
import serverSide.apis.WishListApis;
import serverSide.apis.WishListItemApis;
import serverSide.dbLayer.ContributionHandler;
import serverSide.dbLayer.FriendsHandler;
import serverSide.dbLayer.ItemHandler;
import serverSide.dbLayer.NotificationHandler;
import serverSide.dbLayer.UsersHandler;
import serverSide.dbLayer.WishListHandler;
import serverSide.dbLayer.WishListItemHandler;
import serverSide.services.NotificationService;

public class RequestRouter {

    private static UsersHandler usersHandler;
    private static ContributionHandler contributionHandler;
    private static FriendsHandler friendsHandler;
    private static ItemHandler itemHandler;
    private static WishListHandler wishListHandler;
    private static WishListItemHandler wishListItemHandler;
    private static NotificationHandler notificationHandler;

    // APIs
    private static UserApis userApis;
    private static FriendsApis friendsApis;
    private static ItemApis itemApis;
    private static WishListApis wishListApis;
    private static WishListItemApis wishListItemApis;
    private static NotificationApis notificationApis;
    private static ContributionApis contributionApis;
    private static NotificationService notificationService;

    static {
        itemHandler = new ItemHandler();
        friendsHandler = new FriendsHandler();
        wishListHandler = new WishListHandler(friendsHandler);
        usersHandler = new UsersHandler(wishListHandler);
        contributionHandler = new ContributionHandler(wishListHandler, usersHandler);
        notificationHandler = new NotificationHandler();
        wishListItemHandler = new WishListItemHandler(itemHandler, wishListHandler, contributionHandler);
        notificationService = new NotificationService(notificationHandler);

        // APIs wiring
        wishListApis = new WishListApis(wishListHandler);
        userApis = new UserApis(usersHandler);
        friendsApis = new FriendsApis(friendsHandler, notificationService, userApis);
        itemApis = new ItemApis(itemHandler);
        contributionApis = new ContributionApis(contributionHandler, wishListApis, userApis, notificationService);
        wishListItemApis = new WishListItemApis(wishListItemHandler, contributionApis , notificationService , wishListApis,userApis);
        notificationApis = new NotificationApis(notificationHandler);
    }

    public static Object handleRequest(Request request) {

        // ===== Users =====
        if (request instanceof LoginRequest) return userApis.login((LoginRequest) request);
        if (request instanceof RegisterationRequest) return userApis.register((RegisterationRequest) request);
        if (request instanceof User) return userApis.register((User) request);
        if (request instanceof HasEnoughBalanceRequest) return userApis.hasEnoughBalance((HasEnoughBalanceRequest) request);
        if (request instanceof UpdateBalanceRequest) return userApis.updateBalance((UpdateBalanceRequest) request);

        // ===== Contribution =====
        if (request instanceof AddContributionRequest) return contributionApis.addContribution((AddContributionRequest) request);
        if (request instanceof RemoveContributionRequest) return contributionApis.removeContribution((RemoveContributionRequest) request);

        // ===== Friends =====
        if (request instanceof AddFriendRequest) return friendsApis.addFriend((AddFriendRequest) request);
        if (request instanceof AcceptFriendRequest) return friendsApis.acceptFriend((AcceptFriendRequest) request);
        if (request instanceof GetFriendsRequest) return friendsApis.getFriends((GetFriendsRequest) request);
        if (request instanceof GetPendingFriendsRequest) return friendsApis.getPending((GetPendingFriendsRequest) request);
        if (request instanceof RejectFriendRequest) return friendsApis.reject((RejectFriendRequest) request);
        if (request instanceof GetNonFriendsRequest) return friendsApis.getNonFriends((GetNonFriendsRequest) request);
        if (request instanceof RemoveFriendRequest) return friendsApis.removeFriend((RemoveFriendRequest) request);

        // ===== Item =====
        if (request instanceof AddItemRequest) return itemApis.addItem((AddItemRequest) request);
        if (request instanceof DeleteItemRequest) return itemApis.deleteItem((DeleteItemRequest) request);
        if (request instanceof GetItemPriceRequest) return itemApis.getItemPrice((GetItemPriceRequest) request);
        if (request instanceof GetItemByIdRequest) return itemApis.getItemById((GetItemByIdRequest) request);
        if (request instanceof GetAllItemsRequest) return itemApis.getAllItems((GetAllItemsRequest) request);

        // ===== WishList =====
        if (request instanceof GetWishListByUserIdRequest) return wishListApis.getWishListByUserId((GetWishListByUserIdRequest) request);
        if (request instanceof GetFriendsWishListsRequest) return wishListApis.getFriendsWishLists((GetFriendsWishListsRequest) request);
        if (request instanceof UpdateWishListCurrentAmountRequest) return wishListApis.updateCurrentAmount((UpdateWishListCurrentAmountRequest) request);
        if (request instanceof DeleteWishListRequest) return wishListApis.deleteWishList((DeleteWishListRequest) request);

        // ===== WishListItem =====
        if (request instanceof AddWishListItemRequest) return wishListItemApis.addWishListItem((AddWishListItemRequest) request);
        if (request instanceof RemoveWishListItemRequest) return wishListItemApis.removeWishListItem((RemoveWishListItemRequest) request);
        if (request instanceof GetWishListItemsRequest) return wishListItemApis.getWishListItems((GetWishListItemsRequest) request);

        // ===== Notifications =====
        if (request instanceof AddNotificationRequest) return notificationApis.addNotification((AddNotificationRequest) request);
        if (request instanceof GetNotificationsRequest) return notificationApis.getNotifications((GetNotificationsRequest) request);
        if (request instanceof GetUnreadNotificationsRequest) return notificationApis.getUnread((GetUnreadNotificationsRequest) request);
        if (request instanceof MarkNotificationAsReadRequest) return notificationApis.markOneRead((MarkNotificationAsReadRequest) request);
        if (request instanceof MarkAllNotificationsAsReadRequest) return notificationApis.markAllRead((MarkAllNotificationsAsReadRequest) request);
        if (request instanceof dtos.requestDtos.notificationHandler.ClearNotificationRequest) return notificationApis.clearNotification((dtos.requestDtos.notificationHandler.ClearNotificationRequest) request);
        if (request instanceof dtos.requestDtos.notificationHandler.ClearAllNotificationsRequest) return notificationApis.clearAllNotifications((dtos.requestDtos.notificationHandler.ClearAllNotificationsRequest) request);

        return null;
    }
}
