package clientSide.controllers;

import models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MarketItemCardController {

    @FXML
    private ImageView itemImage;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Button addToWishlistBtn;

    private Item item;
    private int targetWishListId;

    public void setData(Item item, int targetWishListId) {
        this.item = item;
        this.targetWishListId = targetWishListId;

        if (item != null) {
            if (itemNameLabel != null) itemNameLabel.setText(item.getName());
            if (priceLabel != null) priceLabel.setText(String.format("$%.2f", item.getPrice()));
            // Image logic placeholder
        }
    }

    @FXML
    public void onAddToWishlistClicked() {
        if (item == null || targetWishListId <= 0) {
            System.out.println("Cannot add item: Invalid item or wishlist ID.");
            return;
        }

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn != null) {
                System.out.println("Adding item " + item.getName() + " to wishlist " + targetWishListId);
                dtos.requestDtos.wishListItemHandler.AddWishListItemRequest req = 
                    new dtos.requestDtos.wishListItemHandler.AddWishListItemRequest(targetWishListId, item.getItemId());
                
                Object response = conn.sendAndWait(req);
                if (response instanceof Boolean && (Boolean) response) {
                    System.out.println("Item added successfully!");
                    if(addToWishlistBtn != null) {
                        addToWishlistBtn.setText("Added");
                        addToWishlistBtn.setDisable(true);
                    }
                } else {
                    System.out.println("Failed to add item to wishlist.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
