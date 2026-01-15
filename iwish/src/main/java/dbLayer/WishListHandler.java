package dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.WishList;
public class WishListHandler extends DBHandler {

    private FriendsHandler friendsHandler;
    public WishListHandler(FriendsHandler friendsHandler) {
        super("Wishlist");
        this.friendsHandler = friendsHandler;
    }

    public WishList addNewWishList(WishList wishList) {
        String query = "INSERT INTO " + tableName + " (current_amount, total_amount, user_id) VALUES (?, ?, ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setDouble(1, wishList.getCurrentAmount());
            pstmt.setDouble(2, wishList.getTotalAmount());
            pstmt.setInt(3, wishList.getUser().getUserId());
            pstmt.executeUpdate();
            resultSet = pstmt.getGeneratedKeys();
            if (resultSet.next()) {
                int generatedId = resultSet.getInt(1);
                wishList.setWishListId(generatedId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return wishList;
    }
    public WishList getWishListByUserId(int userId) {
        String query = "SELECT * FROM " + tableName + " WHERE user_id = ?";
        WishList wishList = null;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                wishList = new WishList();
                wishList.setWishListId(resultSet.getInt("wishlist_id"));
                wishList.setCurrentAmount(resultSet.getDouble("current_amount"));
                wishList.setTotalAmount(resultSet.getDouble("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return wishList;
    }   
    public List<WishList> getFriendsWishLists(int userId) {
        List<WishList> friendsWishLists = new ArrayList<>();
        List<models.User> friends = friendsHandler.getFriendsByUserId(userId);
        for (models.User friend : friends) {
            WishList wishList = getWishListByUserId(friend.getUserId());
            if (wishList != null) {
                wishList.setUser(friend);
                friendsWishLists.add(wishList);
            }
        }
        return friendsWishLists;
    }
    public boolean updateWishListCurrentAmount(int wishListId, double newAmount , char operation) {
        String query = "UPDATE " + tableName + " SET current_amount = current_amount " + operation + " ? WHERE wishlist_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDouble(1, newAmount);
            pstmt.setInt(2, wishListId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
        return true;

    }
    public void updateWishListTotalAmount(int wishListId, double amount , char operation) {
        String query = "UPDATE " + tableName + " SET total_amount = total_amount " + operation + " ? WHERE wishlist_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, wishListId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();   
        } finally {
            close();
        }
    }
    public boolean deleteWishList(int wishListId) {
        String query = "DELETE FROM " + tableName + " WHERE wishlist_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
        return true;
    }
}