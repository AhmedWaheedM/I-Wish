package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dtos.NotificationDTO;
import serverSide.SessionManager;
import models.WishList;

public class ContributionHandler extends  DBHandler {

    private WishListHandler wishListHandler;
    private UsersHandler userHandler;
    public ContributionHandler(WishListHandler wishListHandler, UsersHandler userHandler) {
        super("Contribution");
        this.wishListHandler = wishListHandler;
        this.userHandler = userHandler;
    }

    public double getContributionAmount(int contributionId) {
        String query = "SELECT amount FROM " + tableName + " WHERE id = ?";
        double amount = -1;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, contributionId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                amount = resultSet.getDouble("amount");
            } else {
                System.out.println("No contribution found with ID " + contributionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return amount;
    }
    
    public boolean addContribution(int userId, int wishListId, double amount) {

        if(userHandler.hasEnoughBalance(userId, amount)) {
            userHandler.updateBalance(userId, amount, '-');
        } else {
            System.out.println("User with ID " + userId + " does not have enough balance to contribute " + amount);
            return false;
        }
        String query = "INSERT INTO "+ tableName +" (user_id, wishlist_id, amount) VALUES (?, ?, ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wishListId);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        wishListHandler.updateWishListCurrentAmount(wishListId, amount, '+');

        int ownerId = getOwnerIdByWishListId(wishListId);
        SessionManager.sendNotification(ownerId, 
            new NotificationDTO("A friend contributed " + amount + " to your gift!", "NEW_CONTRIBUTION"));

        checkAndNotify(wishListId); 
        return true;
    }

    private void checkAndNotify(int wishListId) {
        WishList wl = wishListHandler.getWishListByUserId(wishListId);
        if (wl != null && wl.getCurrentAmount() >= wl.getTotalAmount()) {
            int ownerId = getOwnerIdByWishListId(wishListId);
            SessionManager.sendNotification(ownerId, 
                new NotificationDTO("Congratulations! Your gift is fully funded!", "GIFT_COMPLETE"));

            List<Integer> contributors = getContributorsByWishListId(wishListId);
            for (Integer contributorId : contributors) {
                if (contributorId != ownerId) {
                    SessionManager.sendNotification(contributorId, 
                        new NotificationDTO("The gift you contributed to is now complete!", "GIFT_COMPLETE"));
                }
            }
        }
    }

    private List<Integer> getContributorsByWishListId(int wishListId) {
        List<Integer> contributorIds = new ArrayList<>();
        String query = "SELECT DISTINCT user_id FROM " + tableName + " WHERE wishlist_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                contributorIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return contributorIds;
    }
    private int getOwnerIdByWishListId(int wishListId) {
        String query = "SELECT user_id FROM Wishlist WHERE wishlist_id = ?";
        int ownerId = -1;
        try {
            connect();
            java.sql.PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ownerId = rs.getInt("user_id");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ownerId;
    }
    public Boolean removeContribution(int contributionId , int userId , int wishListId) {
        double amount = getContributionAmount(contributionId);
        if(amount == -1) {
            return false;
        }
        String query = "DELETE FROM " + tableName + " WHERE id = ? AND user_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, contributionId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No contribution found with ID " + contributionId + " for user ID " + userId);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
        userHandler.updateBalance(userId, amount, '+');
        wishListHandler.updateWishListCurrentAmount(wishListId, amount, '-');
        return true;
    }


}
