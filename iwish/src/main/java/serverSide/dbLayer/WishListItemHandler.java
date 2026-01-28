package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
public class WishListItemHandler extends DBHandler {

    private ItemHandler itemHandler;
    private WishListHandler wishListHandler;
    private ContributionHandler contributionHandler;
    
    public WishListItemHandler(ItemHandler itemHandler, WishListHandler wishListHandler, ContributionHandler contributionHandler) {
        super("wishlist_item");
        this.itemHandler = itemHandler;
        this.wishListHandler = wishListHandler;
        this.contributionHandler = contributionHandler;
    }
    public void addWishListItem(int wishListId, int itemId) {
        double itemPrice = itemHandler.getItemPrice(itemId);
        if(getItemQuantityInWishList(wishListId, itemId) == 0){
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
                wishListHandler.updateWishListTotalAmount(wishListId, itemPrice , '+');
                close();
            }
        }
        else{
            String query = "UPDATE " + tableName + " SET quantity = quantity + 1 WHERE wishlist_id = ? AND item_id = ?";
            try {
                connect();
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, wishListId);
                pstmt.setInt(2, itemId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                wishListHandler.updateWishListTotalAmount(wishListId, itemPrice , '+');
                close();
            }
        }

    }
    public void removeWishListItem(int wishListId, int itemId) {

        String decQuery =
            "UPDATE " + tableName +
            " SET quantity = quantity - 1 " +
            " WHERE wishlist_id = ? AND item_id = ? AND quantity > 1";

        String delQuery =
            "DELETE FROM " + tableName +
            " WHERE wishlist_id = ? AND item_id = ? AND quantity = 1";

        try {
            connect();

            // 1) Try decrement if quantity > 1
            PreparedStatement dec = connection.prepareStatement(decQuery);
            dec.setInt(1, wishListId);
            dec.setInt(2, itemId);
            int updated = dec.executeUpdate();

            // 2) If not decremented, delete if quantity == 1
            if (updated == 0) {
                PreparedStatement del = connection.prepareStatement(delQuery);
                del.setInt(1, wishListId);
                del.setInt(2, itemId);
                del.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to remove/decrease item quantity in wishlist");
            return; // don't update total if DB failed
        } finally {
            close();
        }

        // Subtract ONE unit price (because we removed one unit)
        double itemPrice = itemHandler.getItemPrice(itemId);
        wishListHandler.updateWishListTotalAmount(wishListId, itemPrice, '-');
    }

    public java.util.List<models.WishListItem> getWishListItems(int wishListId) {
        System.err.println("here");
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
                item.setRecId(resultSet.getInt("rec_id"));
                item.setWishListId(resultSet.getInt("wishlist_id"));
                item.setQuantity(resultSet.getInt("quantity"));
                int itemId = resultSet.getInt("item_id");
                models.Item itemObj = itemHandler.getItemById(itemId); 
                item.setItem(itemObj);
                
                // Assign contributions
                java.util.List<models.Contribution> itemContributions = new java.util.ArrayList<>();
                for (models.Contribution c : contributions) {
                    if (c.getWishListItem() != null && c.getWishListItem().getRecId() == item.getRecId()) {
                        itemContributions.add(c);
                    }
                }
                item.setContributions(itemContributions);

                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching wishlist items: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
        System.err.println("DEBUG: Fetched " + items.size() + " items for wishlist ID " + wishListId);
        return items;
    }


    public Integer getRecIdByWishListAndItem(int wishListId, int itemId) {
        String q = "SELECT rec_id FROM " + tableName + " WHERE wishlist_id = ? AND item_id = ?";
        try {
            connect();
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setInt(1, wishListId);
            ps.setInt(2, itemId);
            resultSet = ps.executeQuery();
            if (resultSet.next()) return resultSet.getInt("rec_id");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }
        return null;
    }

    private Integer getItemQuantityInWishList(int wishListId, int itemId) {
        String query = "SELECT COUNT(*) AS count FROM " + tableName + " WHERE wishlist_id = ? AND item_id = ?";
        Integer quantity = 0;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, wishListId);
            pstmt.setInt(2, itemId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                quantity = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }
        return quantity;
    }
}