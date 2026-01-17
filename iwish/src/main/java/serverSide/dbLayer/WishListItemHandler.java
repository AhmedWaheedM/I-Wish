package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
public class WishListItemHandler extends DBHandler {

    private ItemHandler itemHandler;
    private WishListHandler wishListHandler;
    
    public WishListItemHandler(ItemHandler itemHandler, WishListHandler wishListHandler) {
        super("WishListItem");
        this.itemHandler = itemHandler;
        this.wishListHandler = wishListHandler;
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
}