package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        return true;
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


    public java.util.List<models.Contribution> getContributionsByWishListId(int wishListId) {
        java.util.List<models.Contribution> contributions = new java.util.ArrayList<>();
        String query = "SELECT c.amount, u.user_id, u.username, wi.rec_id " +
                       "FROM " + tableName + " c " +
                       "JOIN Wishlist_Item wi ON c.wishlist_item_id = wi.rec_id " +
                       "JOIN User u ON c.contributor_id = u.user_id " +
                       "WHERE wi.wishlist_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                models.Contribution c = new models.Contribution();
                // c.setId(0); // No single ID
                c.setAmount(resultSet.getDouble("amount"));
                
                models.User contributor = new models.User();
                contributor.setUserId(resultSet.getInt("user_id"));
                contributor.setUserName(resultSet.getString("username"));
                c.setUser(contributor);
                
                // Optional: set item ID if needed by client filtering
                // models.WishListItem item = new models.WishListItem();
                // item.setRecId(resultSet.getInt("rec_id"));
                // c.setWishListItem(item);

                contributions.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return contributions;
    }
}
