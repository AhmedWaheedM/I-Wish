package serverSide.dbLayer;

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
            pstmt.setString(3, "PENDING");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void acceptFriend(User user1, User user2) {
        // user1 sent the request, user2 is accepting it.
        // We need to update the row where user1_id = u1 AND user2_id = u2
        String query = "UPDATE " + tableName + " SET status = ? WHERE user1_id = ? AND user2_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, "ACCEPTED");
            pstmt.setInt(2, user1.getUserId());
            pstmt.setInt(3, user2.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public List<User> getFriendsByUserId(int userId) {
        String query = "SELECT user1_id, user2_id FROM " + tableName + " WHERE (user1_id = ? OR user2_id = ?) AND status = ?";
        List<User> friends = new ArrayList<>();
        // Need to fetch user details. Use UsersHandler logic directly or duplicate query? 
        // Instantiating UsersHandler here might technically be creating a new connection each time if not managed well, 
        // but DBHandler base class manages connection per method call mostly.
        // Better: Join Query. But let's use the helper for safety/consistency if easy.
        // Actually, let's just do a JOIN query here, it's efficient.
        
        // Revised Query with JOIN
        // join on the "other" user
        String joinQuery = "SELECT u.user_id, u.username, u.balance FROM " + tableName + " f " +
                           "JOIN User u ON (u.user_id = CASE WHEN f.user1_id = ? THEN f.user2_id ELSE f.user1_id END) " +
                           "WHERE (f.user1_id = ? OR f.user2_id = ?) AND f.status = ?";
        
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(joinQuery);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setString(4, "ACCEPTED");
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                User friend = new User();
                friend.setUserId(resultSet.getInt("user_id"));
                friend.setUserName(resultSet.getString("username"));
                friend.setBalance(resultSet.getDouble("balance"));
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
        // Pending requests sent TO the user. 
        // If I am user2, I want to see user1.
        // If I sent the request (user1), I might want to see it too, but usually "Friend Requests" are incoming.
        // Assuming "Friend Requests" tab shows INCOMING requests.
        // Incoming: user2_id = me, status = PENDING.
        
        String query = "SELECT u.user_id, u.username, u.balance FROM " + tableName + " f " +
                       "JOIN User u ON u.user_id = f.user1_id " +
                       "WHERE f.user2_id = ? AND f.status = ?";
        
        List<User> pendingFriends = new ArrayList<>();
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, "PENDING");
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                User pendingFriend = new User();
                pendingFriend.setUserId(resultSet.getInt("user_id"));
                pendingFriend.setUserName(resultSet.getString("username"));
                pendingFriend.setBalance(resultSet.getDouble("balance"));
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

    public void removeFriend(User user1, User user2) {
        // Same logic as reject/cancel
        rejectFriendRequest(user1, user2);
    }

    public List<User> getNonFriends(int userId) {
        // Select users who are NOT me AND NOT in the friends table associated with me
        String query = "SELECT * FROM User WHERE user_id != ? AND user_id NOT IN (" +
                       "SELECT CASE WHEN user1_id = ? THEN user2_id ELSE user1_id END " +
                       "FROM " + tableName + " WHERE user1_id = ? OR user2_id = ?)";
        
        List<User> nonFriends = new ArrayList<>();
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                User u = new User();
                u.setUserId(resultSet.getInt("user_id"));
                u.setUserName(resultSet.getString("username"));
                u.setBalance(resultSet.getDouble("balance"));
                nonFriends.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return nonFriends;
    }
}