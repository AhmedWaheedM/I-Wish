package serverSide.apis;

import dtos.Notification; // realtime DTO
import dtos.requestDtos.friendsHandler.AddFriendRequest;
import dtos.requestDtos.friendsHandler.GetFriendsRequest;
import dtos.requestDtos.friendsHandler.GetPendingFriendsRequest;
import dtos.requestDtos.friendsHandler.RejectFriendRequest;
import models.User;
import serverSide.NotificationManger;
import serverSide.dbLayer.FriendsHandler;
import serverSide.dbLayer.NotificationHandler;

public class FriendsApis {

    private final FriendsHandler friendsHandler;
    private final NotificationHandler notificationHandler;
    private final UserApis userApis;

    public FriendsApis(FriendsHandler friendsHandler,
                       NotificationHandler notificationHandler,
                       UserApis userApis) {
        this.friendsHandler = friendsHandler;
        this.notificationHandler = notificationHandler;
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

        models.Notification saved =
            notificationHandler.addNotification(r.getUser2Id(), title, body);

        try {
            Notification realtime = new Notification(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(r.getUser2Id(), realtime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public Object acceptFriend(dtos.requestDtos.friendsHandler.AcceptFriendRequest r) {

        User u1 = new User(); u1.setUserId(r.getUser1Id()); // sender
        User u2 = new User(); u2.setUserId(r.getUser2Id()); // accepter

        friendsHandler.acceptFriend(u1, u2);

        String accepterName = userApis.getUserNameById(r.getUser2Id());

        String title = "Friend Request Accepted ✅";
        String body = accepterName + " accepted your friend request.";

        models.Notification saved =
            notificationHandler.addNotification(r.getUser1Id(), title, body);

        try {
            Notification realtime = new Notification(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(r.getUser1Id(), realtime);
        } catch (Exception ignored) {}

        return true;
    }

    public Object getFriends(GetFriendsRequest r) {
        return friendsHandler.getFriendsByUserId(r.getUserId());
    }

    public Object getPending(GetPendingFriendsRequest r) {
        return friendsHandler.getPendingFriendsByUserId(r.getUserId());
    }

    public Object reject(RejectFriendRequest r) {

        User sender = new User(); sender.setUserId(r.getUser1Id());
        User receiver = new User(); receiver.setUserId(r.getUser2Id());

        friendsHandler.rejectFriendRequest(sender, receiver);

        String receiverName = userApis.getUserNameById(r.getUser2Id());

        String title = "Friend Request Rejected ❌";
        String body = receiverName + " rejected your friend request.";

        models.Notification saved =
            notificationHandler.addNotification(r.getUser2Id(), title, body);

        try {
            Notification realtime = new Notification(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(r.getUser2Id(), realtime);
        } catch (Exception ignored) {}

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

        models.Notification saved =
            notificationHandler.addNotification(r.getUser2Id(), title, body);

        try {
            Notification realtime = new Notification(saved.getTitle(), saved.getBody());
            NotificationManger.sendNotificaiton(r.getUser2Id(), realtime);
        } catch (Exception ignored) {}

        return true;
    }
}
