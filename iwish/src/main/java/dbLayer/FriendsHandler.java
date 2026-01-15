package dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.User;

public class FriendsHandler extends DBHandler {
    public FriendsHandler() {
        super("Friends");
    }

    public void addFriend(User user1, User user2) {
        String query = "INSERT INTO " + tableName + " (user1_id, user2_id, status) VALUES (?, ?, ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, user1.getUserId());
            pstmt.setInt(2, user2.getUserId());
            pstmt.setString(3, "ACCEPTED");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public List<User> getFriendsByUserId(int userId) {
        String query = "SELECT user1_id, user2_id FROM " + tableName + " WHERE user1_id = ? OR user2_id = ? AND status = ?";
        List<User> friends = new ArrayList<>();
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, "ACCEPTED");
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int friendId = (resultSet.getInt("user1_id") == userId) ? resultSet.getInt("user2_id") : resultSet.getInt("user1_id");
                User friend = new User();
                friend.setUserId(friendId);
                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return friends;
    }
    public List<User> getPendingFriendsByUserId(int userId) {
        String query = "SELECT user1_id, user2_id FROM " + tableName + " WHERE (user1_id = ? OR user2_id = ?) AND status = ?";
        List<User> pendingFriends = new ArrayList<>();
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, "PENDING");
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int pendingFriendId = (resultSet.getInt("user1_id") == userId) ? resultSet.getInt("user2_id") : resultSet.getInt("user1_id");
                User pendingFriend = new User();
                pendingFriend.setUserId(pendingFriendId);
                pendingFriends.add(pendingFriend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pendingFriends;
    }
    public void rejectFriendRequest(User user1, User user2) {
        String query = "DELETE FROM " + tableName + " WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, user1.getUserId());
            pstmt.setInt(2, user2.getUserId());
            pstmt.setInt(3, user2.getUserId());
            pstmt.setInt(4, user1.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}