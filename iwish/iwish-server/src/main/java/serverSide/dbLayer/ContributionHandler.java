package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContributionHandler extends  DBHandler {

    private WishListHandler wishListHandler;
    private UsersHandler userHandler;
    public ContributionHandler(WishListHandler wishListHandler, UsersHandler userHandler ) {
        super("contribution");
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
            return -1;
        } finally {
            close();
        }
        return amount;
    }
    public boolean addContribution(int userId, int wishListId, int wishListItemId, double amount) {

        if(userHandler.hasEnoughBalance(userId, amount)) {
            userHandler.updateBalance(userId, amount, '-');
        } else {
            System.out.println("User with ID " + userId + " does not have enough balance to contribute " + amount);
            return false;
        }
        String query = "INSERT INTO "+ tableName +" (contributor_id, wishlist_item_id, amount) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE amount = amount + ?";
        try {
            connect();
            System.out.println("Contribution in progress");
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wishListItemId); // Use item ID here
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, amount); // For update
            pstmt.executeUpdate();
            System.out.println("Contribution added successfully");
        } catch (SQLException e) {
            System.out.println("Contribution failed");
            e.printStackTrace();
            return false;
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
        String query = "DELETE FROM " + tableName + " WHERE id = ? AND contributor_id = ?";
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
                
                models.WishListItem item = new models.WishListItem();
                item.setRecId(resultSet.getInt("rec_id"));
                c.setWishListItem(item);

                contributions.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
        return contributions;
    }
    public boolean isItemFullyFunded(int wishListItemRecId) {
        String query =
            "SELECT wi.quantity AS qty, i.price AS price, " +
            "       COALESCE(SUM(c.amount), 0) AS contributed " +
            "FROM wishlist_item wi " +
            "JOIN item i ON i.item_id = wi.item_id " +
            "LEFT JOIN contribution c ON c.wishlist_item_id = wi.rec_id " +
            "WHERE wi.rec_id = ? " +
            "GROUP BY wi.rec_id, wi.quantity, i.price";

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListItemRecId);

            resultSet = pstmt.executeQuery();
            if (!resultSet.next()) return false;

            int qty = resultSet.getInt("qty");
            double price = resultSet.getDouble("price");
            double contributed = resultSet.getDouble("contributed");

            double required = price * qty;

            System.err.println("Required: " + required + ", Contributed: " + contributed);
            return contributed + 0.0001 >= required;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public java.util.List<Integer> getContributorUserIdsForItem(int wishListItemRecId) {
        java.util.List<Integer> userIds = new java.util.ArrayList<>();

        String query =
            "SELECT contributor_id " +
            "FROM " + tableName + " " +
            "WHERE wishlist_item_id = ?";

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListItemRecId);

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                userIds.add(resultSet.getInt("contributor_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }

        return userIds;
    }

    public double getUserContributionToItem(int userId, int wishListItemRecId) {
        double amount = 0;

        String query =
            "SELECT amount " +
            "FROM " + tableName + " " +
            "WHERE contributor_id = ? AND wishlist_item_id = ?";

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wishListItemRecId);

            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                amount = resultSet.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }

        return amount;
    }

    public void removeUserContributionsForItem(int userId, int wishListItemRecId) {
        String query =
            "DELETE FROM " + tableName + " " +
            "WHERE contributor_id = ? AND wishlist_item_id = ?";

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wishListItemRecId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
