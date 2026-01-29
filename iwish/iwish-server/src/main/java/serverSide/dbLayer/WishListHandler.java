package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.WishList;
public class WishListHandler extends DBHandler {

    private FriendsHandler friendsHandler;
    public WishListHandler(FriendsHandler friendsHandler) {
        super("wishlist");
        this.friendsHandler = friendsHandler;
    }

    public WishList addNewWishList(WishList wishList) {
        String query = "INSERT INTO " + tableName + " (current_amount, total_items_amount, user_id) VALUES (?, ?, ?)";
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
            return null;
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
                wishList.setTotalAmount(resultSet.getDouble("total_items_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
        String query = "UPDATE " + tableName + " SET total_items_amount = total_items_amount " + operation + " ? WHERE wishlist_id = ?";
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
    public Integer getUserIdByWishListId(int wishListId) {
        String query = "SELECT user_id FROM " + tableName + " WHERE wishlist_id = ?";

        Integer userId = null;

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);

            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }

        return userId;
    }
    public Double getWishListTotalAmount(int wishListId) {
        String query = "SELECT total_items_amount FROM " + tableName + " WHERE wishlist_id = ?";

        Double totalAmount = null;

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);

            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                totalAmount = resultSet.getDouble("total_items_amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }

        return totalAmount;
    }

    public Double getWishListCurrentAmount(int wishListId) {
        String query = "SELECT current_amount FROM " + tableName + " WHERE wishlist_id = ?";

        Double currentAmount = null;

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);

            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                currentAmount = resultSet.getDouble("current_amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
        return currentAmount;
    }
    public WishList createEmptyWishListForUser(int userId) {
        WishList w = new WishList();
        w.setCurrentAmount(0);
        w.setTotalAmount(0);

        models.User u = new models.User();
        u.setUserId(userId);
        w.setUser(u);

        return addNewWishList(w);
    }


}