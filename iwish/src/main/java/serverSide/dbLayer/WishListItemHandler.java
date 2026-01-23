package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
public class WishListItemHandler extends DBHandler {

    private ItemHandler itemHandler;
    private WishListHandler wishListHandler;
    private ContributionHandler contributionHandler;
    
    public WishListItemHandler(ItemHandler itemHandler, WishListHandler wishListHandler, ContributionHandler contributionHandler) {
        super("Wishlist_Item");
        this.itemHandler = itemHandler;
        this.wishListHandler = wishListHandler;
        this.contributionHandler = contributionHandler;
    }
    public void addWishListItem(int wishListId, int itemId) {
        String query = "INSERT INTO " + tableName + " (wishlist_id, item_id) VALUES (?, ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        double itemPrice = itemHandler.getItemPrice(itemId);
        wishListHandler.updateWishListTotalAmount(wishListId, itemPrice , '+');
    }
    public void removeWishListItem(int wishListId, int itemId) {
        String query = "DELETE FROM " + tableName + " WHERE wishlist_id = ? AND item_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        double itemPrice = itemHandler.getItemPrice(itemId);
        wishListHandler.updateWishListTotalAmount(wishListId, itemPrice , '-');
    }

    public java.util.List<models.WishListItem> getWishListItems(int wishListId) {
        java.util.List<models.WishListItem> items = new java.util.ArrayList<>();
        String query = "SELECT * FROM " + tableName + " WHERE wishlist_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            resultSet = pstmt.executeQuery();
            
            // Optimization: Fetch contributions once for the wishlist
            java.util.List<models.Contribution> contributions = contributionHandler.getContributionsByWishListId(wishListId);
            
            while (resultSet.next()) {
                models.WishListItem item = new models.WishListItem();
                item.setRecId(resultSet.getInt("wishlist_item_id"));
                item.setWishListId(resultSet.getInt("wishlist_id"));
                
                int itemId = resultSet.getInt("item_id");
                models.Item itemObj = itemHandler.getItemById(itemId); 
                item.setItem(itemObj);
                
                // Assign contributions
                item.setContributions(contributions);

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return items;
    }
}