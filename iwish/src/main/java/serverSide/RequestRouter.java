package serverSide;



import dtos.Notification;
import dtos.Request;
import dtos.requestDtos.Item.AddItemRequest;
import dtos.requestDtos.Item.DeleteItemRequest;
import dtos.requestDtos.Item.GetAllItemsRequest;
import dtos.requestDtos.Item.GetItemByIdRequest;
import dtos.requestDtos.Item.GetItemPriceRequest;
import dtos.requestDtos.contributionHandler.AddContributionRequest;
import dtos.requestDtos.contributionHandler.RemoveContributionRequest;
import dtos.requestDtos.friendsHandler.AddFriendRequest;
import dtos.requestDtos.friendsHandler.GetFriendsRequest;
import dtos.requestDtos.friendsHandler.GetPendingFriendsRequest;
import dtos.requestDtos.friendsHandler.RejectFriendRequest;
import dtos.requestDtos.userHandler.HasEnoughBalanceRequest;
import dtos.requestDtos.userHandler.LoginRequest;
import dtos.requestDtos.userHandler.UpdateBalanceRequest;
import dtos.requestDtos.wishListHandler.DeleteWishListRequest;
import dtos.requestDtos.wishListHandler.GetFriendsWishListsRequest;
import dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest;
import dtos.requestDtos.wishListHandler.UpdateWishListCurrentAmountRequest;
import dtos.requestDtos.wishListItemHandler.AddWishListItemRequest;
import dtos.requestDtos.wishListItemHandler.RemoveWishListItemRequest;
import models.User;
import serverSide.dbLayer.ContributionHandler;
import serverSide.dbLayer.FriendsHandler;
import serverSide.dbLayer.ItemHandler;
import serverSide.dbLayer.UsersHandler;
import serverSide.dbLayer.WishListHandler;
import serverSide.dbLayer.WishListItemHandler;

public class RequestRouter {

    private static UsersHandler usersHandler;
    private static ContributionHandler contributionHandler;
    private static FriendsHandler friendsHandler;
    private static ItemHandler itemHandler;
    private static WishListHandler wishListHandler;
    private static WishListItemHandler wishListItemHandler;

    static {
        usersHandler = new UsersHandler();

        itemHandler = new ItemHandler();

        friendsHandler = new FriendsHandler();

        wishListHandler = new WishListHandler(friendsHandler);

        contributionHandler = new ContributionHandler(wishListHandler, usersHandler);

        wishListItemHandler = new WishListItemHandler(itemHandler, wishListHandler);
    }

    public static Object handleRequest(Request request) {

        

        // ===== Users =====
        if (request instanceof LoginRequest) {
            LoginRequest r = (LoginRequest) request;
            return usersHandler.Login(r.getUserName(), r.getPassword());
        }

        if (request instanceof User) {
            User user = (User) request;
            return usersHandler.register(user);
        }

        if (request instanceof HasEnoughBalanceRequest) {
            HasEnoughBalanceRequest r = (HasEnoughBalanceRequest) request;
            return usersHandler.hasEnoughBalance(r.getUserId(), r.getAmount());
        }

        if (request instanceof UpdateBalanceRequest) {
            UpdateBalanceRequest r = (UpdateBalanceRequest) request;

            usersHandler.updateBalance(r.getUserId(), r.getAmount(), r.getOperation());
            return true;
        }

        // ===== Contribution =====
        if (request instanceof AddContributionRequest) {
            AddContributionRequest r = (AddContributionRequest) request;
            Integer userId = wishListHandler.getUserIdByWishListId(r.getWishListId());
            double wishListTotalAmount = wishListHandler.getWishListTotalAmount(r.getWishListId());
            String contributorName = usersHandler.getUserNameById(userId);

            Notification notification = new Notification(
                "New Contribution 🎁",
                contributorName + " contributed " + r.getAmount() +
                " to your wishlist.\n" +
                "Total amount: " + wishListTotalAmount + "\n" +
                "Remaining amount: " + (wishListTotalAmount - r.getAmount())
            );
            NotificationManger.sendNotificaiton(userId, notification);
            
            return contributionHandler.addContribution(r.getUserId(), r.getWishListId(), r.getAmount());
        }

        if (request instanceof RemoveContributionRequest) {
            RemoveContributionRequest r = (RemoveContributionRequest) request;
            return contributionHandler.removeContribution(r.getContributionId(), r.getUserId(), r.getWishListId());
        }

        // ===== Friends =====
        if (request instanceof AddFriendRequest) {
            AddFriendRequest r = (AddFriendRequest) request;

            User u1 = new User();
            u1.setUserId(r.getUser1Id());

            User u2 = new User();
            u2.setUserId(r.getUser2Id());

            friendsHandler.addFriend(u1, u2);
            return true;
        }

        if (request instanceof GetFriendsRequest) {
            GetFriendsRequest r = (GetFriendsRequest) request;
            return friendsHandler.getFriendsByUserId(r.getUserId());
        }

        if (request instanceof GetPendingFriendsRequest) {
            GetPendingFriendsRequest r = (GetPendingFriendsRequest) request;
            return friendsHandler.getPendingFriendsByUserId(r.getUserId());
        }

        if (request instanceof RejectFriendRequest) {
            RejectFriendRequest r = (RejectFriendRequest) request;

            User u1 = new User();
            u1.setUserId(r.getUser1Id());

            User u2 = new User();
            u2.setUserId(r.getUser2Id());

            friendsHandler.rejectFriendRequest(u1, u2);
            return true;
        }

        // ===== Item =====
        if (request instanceof AddItemRequest) {
            AddItemRequest r = (AddItemRequest) request;

            itemHandler.addItem(r.getName(), r.getPrice());
            return true;
        }

        if (request instanceof DeleteItemRequest) {
            DeleteItemRequest r = (DeleteItemRequest) request;

            itemHandler.deleteItem(r.getItemId());
            return true;
        }

        if (request instanceof GetItemPriceRequest) {
            GetItemPriceRequest r = (GetItemPriceRequest) request;
            return itemHandler.getItemPrice(r.getItemId());
        }

        if(request instanceof GetItemByIdRequest){
            GetItemByIdRequest r = ( GetItemByIdRequest) request;
            return itemHandler.getItemById(r.getItemId());
        }

        if(request instanceof GetAllItemsRequest){
            return itemHandler.getAllItems();
        }

        // ===== WishList =====
        if (request instanceof GetWishListByUserIdRequest) {
            GetWishListByUserIdRequest r = (GetWishListByUserIdRequest) request;
            return wishListHandler.getWishListByUserId(r.getUserId());
        }

        if (request instanceof GetFriendsWishListsRequest) {
            GetFriendsWishListsRequest r = (GetFriendsWishListsRequest) request;
            return wishListHandler.getFriendsWishLists(r.getUserId());
        }

        if (request instanceof UpdateWishListCurrentAmountRequest) {
            UpdateWishListCurrentAmountRequest r = (UpdateWishListCurrentAmountRequest) request;
            return wishListHandler.updateWishListCurrentAmount(r.getWishListId(), r.getAmount(), r.getOperation());
        }

        if (request instanceof DeleteWishListRequest) {
            DeleteWishListRequest r = (DeleteWishListRequest) request;
            return wishListHandler.deleteWishList(r.getWishListId());
        }

        // ===== WishListItem =====
        if (request instanceof AddWishListItemRequest) {
            AddWishListItemRequest r = (AddWishListItemRequest) request;

            wishListItemHandler.addWishListItem(r.getWishListId(), r.getItemId());
            return true;
        }

        if (request instanceof RemoveWishListItemRequest) {
            RemoveWishListItemRequest r = (RemoveWishListItemRequest) request;

            wishListItemHandler.removeWishListItem(r.getWishListId(), r.getItemId());
            return true;
        }

        return null; // unknown request
    }
}
