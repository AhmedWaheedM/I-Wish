package clientSide.controllers;

import clientSide.helpers.ItemImageSelector;
import clientSide.helpers.MessageDisplayer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Item;

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
            String name = safe(item.getName());

            if (itemNameLabel != null) itemNameLabel.setText(name);
            if (priceLabel != null) priceLabel.setText(String.format("$%.2f", item.getPrice()));

            loadImage(name);
        }
    }

    private void loadImage(String itemName) {
        if (itemImage == null) return;

        Image img = ItemImageSelector.getImageByItemName(itemName);
        if (img == null) return;

        itemImage.setImage(img);
        itemImage.setPreserveRatio(false);
        itemImage.setSmooth(true);
    }

    private String safe(String s) {
        return s == null ? "" : s;
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
                        new dtos.requestDtos.wishListItemHandler.AddWishListItemRequest(
                                targetWishListId,
                                item.getItemId()
                        );

                Object response = conn.sendAndWait(req);
                if (response instanceof Boolean && (Boolean) response) {
                    System.out.println("Item added successfully!");
                    if (addToWishlistBtn != null) {
                        addToWishlistBtn.setText("Added");
                        addToWishlistBtn.setDisable(true);
                        MessageDisplayer.showSuccess("Item added to wishlist successfully!", "Success");
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
