package serverSide.apis;

import dtos.requestDtos.friendsHandler.AddFriendRequest;
import dtos.requestDtos.friendsHandler.GetFriendsRequest;
import dtos.requestDtos.friendsHandler.GetPendingFriendsRequest;
import dtos.requestDtos.friendsHandler.RejectFriendRequest;
import models.User;
import serverSide.dbLayer.FriendsHandler;
import serverSide.services.NotificationService;

public class FriendsApis {

    private final FriendsHandler friendsHandler;
    private final NotificationService notificationService;
    private final UserApis userApis;

    public FriendsApis(FriendsHandler friendsHandler,
                       NotificationService notificationService,
                       UserApis userApis) {
        this.friendsHandler = friendsHandler;
        this.notificationService = notificationService;
        this.userApis = userApis;
    }

    // ---------------------------
    // Add friend (SEND REQUEST)
    // ---------------------------
    public Object addFriend(AddFriendRequest r) {

        User sender = new User();
        sender.setUserId(r.getUser1Id());

        User receiver = new User();
        receiver.setUserId(r.getUser2Id());

        friendsHandler.addFriend(sender, receiver);

        String senderName = userApis.getUserNameById(r.getUser1Id());

        String title = "New Friend Request 👤";
        String body = senderName + " sent you a friend request.";

        notificationService.notifyUser(r.getUser2Id(), title, body);

        return true;
    }

    public Object acceptFriend(dtos.requestDtos.friendsHandler.AcceptFriendRequest r) {

        User u1 = new User(); u1.setUserId(r.getUser1Id()); // sender
        User u2 = new User(); u2.setUserId(r.getUser2Id()); // accepter

        friendsHandler.acceptFriend(u1, u2);

        String accepterName = userApis.getUserNameById(r.getUser2Id());

        String title = "Friend Request Accepted ✅";
        String body = accepterName + " accepted your friend request.";

        notificationService.notifyUser(r.getUser1Id(), title, body);

        return true;
    }

    public Object getFriends(GetFriendsRequest r) {
        return friendsHandler.getFriendsByUserId(r.getUserId());
    }

    public Object getPending(GetPendingFriendsRequest r) {
        return friendsHandler.getPendingFriendsByUserId(r.getUserId());
    }

    public Object reject(RejectFriendRequest r) {

        System.err.println("r1: " + r.getUser1Id());
        System.err.println("r2: " + r.getUser2Id());


        User sender = new User(); sender.setUserId(r.getUser2Id());
        User receiver = new User(); receiver.setUserId(r.getUser1Id());

        friendsHandler.rejectFriendRequest(sender, receiver);


        String senderName = userApis.getUserNameById(r.getUser1Id());

        String title = "Friend Request Rejected ❌";
        String body = senderName + " rejected your friend request.";

        notificationService.notifyUser(r.getUser2Id(), title, body);

        return true;
    }

    public Object getNonFriends(dtos.requestDtos.friendsHandler.GetNonFriendsRequest r) {
        return friendsHandler.getNonFriends(r.getUserId());
    }

    public Object removeFriend(dtos.requestDtos.friendsHandler.RemoveFriendRequest r) {

        User u1 = new User(); u1.setUserId(r.getUser1Id());
        User u2 = new User(); u2.setUserId(r.getUser2Id());

        friendsHandler.removeFriend(u1, u2);

        String removerName = userApis.getUserNameById(r.getUser1Id());

        String title = "Friend Removed";
        String body = removerName + " removed you from their friends list.";

        notificationService.notifyUser(r.getUser2Id(), title, body);

        return true;
    }
}