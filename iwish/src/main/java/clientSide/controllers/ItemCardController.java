package clientSide.controllers;

import models.WishListItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

public class ItemCardController {

    @FXML
    private ImageView itemImage;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Label ownerLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label collectedLabel;

    @FXML
    private ProgressBar fundingProgress;

    @FXML
    private Label percentLabel;

    @FXML
    private Label remainingLabel;

    public void setData(WishListItem item) {
        // TODO: Fix missing field - methods need to be mapped to models.Item or calculated
        /*
        if (itemNameLabel != null) itemNameLabel.setText(item.getName());
        if (ownerLabel != null) ownerLabel.setText("by " + item.getOwner());
        if (priceLabel != null) priceLabel.setText(String.format("$%.2f", item.getPrice()));
        
        if (collectedLabel != null) {
            collectedLabel.setText(String.format("$%.2f collected", item.getCollected()));
        }

        if (fundingProgress != null) {
            double progress = item.getCollected() / item.getPrice();
            fundingProgress.setProgress(progress);
        }
        
        if (percentLabel != null) {
            double progress = item.getCollected() / item.getPrice();
            percentLabel.setText(String.format("%.0f%% funded", progress * 100));
        }
        
        if (remainingLabel != null) {
             double remaining = item.getPrice() - item.getCollected();
             remainingLabel.setText(String.format("$%.2f remaining", remaining));
        }
        */
        
        if (item.getItem() != null) {
             if (itemNameLabel != null) itemNameLabel.setText(item.getItem().getName());
             if (priceLabel != null) priceLabel.setText(String.format("$%.2f", item.getItem().getPrice()));
        }

        double collected = 0.0;
        if (item.getContributions() != null) {
            for (models.Contribution c : item.getContributions()) {
                collected += c.getAmount();
            }
        }
        
        if (collectedLabel != null) {
            collectedLabel.setText(String.format("$%.2f collected", collected));
        }

        if (item.getItem() != null && item.getItem().getPrice() > 0) {
            double price = item.getItem().getPrice();
            double progress = collected / price;
            
            if (fundingProgress != null) fundingProgress.setProgress(progress);
            if (percentLabel != null) percentLabel.setText(String.format("%.0f%% funded", progress * 100));
            if (remainingLabel != null) {
                double remaining = price - collected;
                remainingLabel.setText(String.format("$%.2f remaining", remaining > 0 ? remaining : 0));
            }
        }
        
        // Owner logic: WishListItem doesn't have direct owner ref, assuming 'Me' for My Wishlist view
        if (ownerLabel != null) ownerLabel.setText("by Me");
    }
}
