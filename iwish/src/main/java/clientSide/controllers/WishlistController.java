package clientSide.controllers;

import models.WishListItem;
import clientSide.controllers.ItemCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WishlistController {

    @FXML
    private GridPane wishlistGrid;

    private List<WishListItem> wishlistItems;

    @FXML
    public void initialize() {
        wishlistItems = new ArrayList<>();
        
        models.User user = clientSide.ClientSession.getInstance().getCurrentUser();
        // If testing without login, user might be null. 
        // In real app, we should redirect or show empty.
        // For debugging, we can fallback or just return if null.
        if (user == null) {
            System.out.println("No user logged in. Cannot fetch wishlist.");
            return; 
        }

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return;

            // 1. Get Wishlist for User
            dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest req1 = 
                new dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest(user.getUserId());
            
            Object response1 = conn.sendAndWait(req1);
            
            if (response1 instanceof models.WishList) {
                models.WishList wishlist = (models.WishList) response1;
                
                // 2. Get Items for Wishlist
                dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest req2 =
                    new dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest(wishlist.getWishListId());
                
                Object response2 = conn.sendAndWait(req2);
                
                if (response2 instanceof java.util.List) {
                    wishlistItems = (java.util.List<WishListItem>) response2;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }

        populateGrid();
    }

    private void populateGrid() {
        wishlistGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (WishListItem item : wishlistItems) {
            try {
                // Load item_card.fxml for each item
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/item_card.fxml"));
                VBox card = loader.load();
                
                // Get the controller and populate data
                ItemCardController cardController = loader.getController();
                cardController.setData(item);

                wishlistGrid.add(card, col, row);
                col++;
                if (col == 3) { // 3 cols
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
