package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import models.WishListItem;
import org.kordamp.ikonli.javafx.FontIcon;

public class ItemCardController {

    @FXML
    private ImageView itemImage;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Label quantityLabel;

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

    @FXML
    private Button removeBtn;

    @FXML
    private Button contributeBtn;
    
    @FXML
    private StackPane completedOverlay;
    
    @FXML
    private StackPane imageContainer;

    private Runnable onRemove;
    private Runnable onContribute;
    private boolean isViewerMode = false;
    private boolean isFullyFunded = false;

    public void setData(WishListItem item) {

        int qty = 1;
        try {
            qty = Math.max(1, item.getQuantity());
        } catch (Exception ignored) {
            qty = 1;
        }

        if (quantityLabel != null) {
            quantityLabel.setText("x" + qty);
        }

        // Name + total price
        double totalPrice = 0;
        if (item.getItem() != null) {
            if (itemNameLabel != null) {
                itemNameLabel.setText(item.getItem().getName());
            }

            double unitPrice = item.getItem().getPrice();
            totalPrice = unitPrice * qty;

            if (priceLabel != null) {
                priceLabel.setText(String.format("$%.2f", totalPrice));
            }
        }

        // Collected sum from contributions
        double collected = 0.0;
        if (item.getContributions() != null) {
            for (models.Contribution c : item.getContributions()) {
                collected += c.getAmount();
            }
        }

        if (collectedLabel != null) {
            collectedLabel.setText(String.format("$%.2f collected", collected));
        }

        // Progress based on TOTAL price (price * quantity)
        double progress = 0;
        if (totalPrice > 0) {
            progress = collected / totalPrice;

            // clamp 0..1
            if (progress < 0) progress = 0;
            if (progress > 1) progress = 1;

            if (fundingProgress != null) {
                fundingProgress.setProgress(progress);
            }

            if (percentLabel != null) {
                percentLabel.setText(String.format("%.0f%% funded", progress * 100));
            }

            if (remainingLabel != null) {
                double remaining = totalPrice - collected;
                remainingLabel.setText(String.format("$%.2f remaining", Math.max(0, remaining)));
            }
        }

        // Check if fully funded (100%)
        isFullyFunded = progress >= 1.0;
        
        // Show completed overlay if fully funded
        if (completedOverlay != null) {
            completedOverlay.setVisible(isFullyFunded);
        }

        // Owner label (adjust if you later support showing real owner)
        if (ownerLabel != null) {
            ownerLabel.setText("by Me");
        }

        updateButtons();
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    public void setOnContribute(Runnable onContribute) {
        this.onContribute = onContribute;
    }

    public void setViewerMode(boolean isViewerMode) {
        this.isViewerMode = isViewerMode;
        updateButtons();
    }

    @FXML
    private void handleRemove() {
        if (onRemove != null) {
            onRemove.run();
        }
    }

    @FXML
    private void handleContribute() {
        if (onContribute != null && !isFullyFunded) {
            onContribute.run();
        }
    }

    private void updateButtons() {
        if (removeBtn == null || contributeBtn == null) return;

        if (isViewerMode) {
            removeBtn.setVisible(false);
            removeBtn.setManaged(false);

            contributeBtn.setVisible(true);
            contributeBtn.setManaged(true);
            
            // If fully funded, change button to "Fully Funded" and disable
            if (isFullyFunded) {
                contributeBtn.setText("Fully Funded");
                contributeBtn.setStyle("-fx-background-color: #6b7280; -fx-cursor: default;");
                contributeBtn.setDisable(true);
                
                // Change icon
                FontIcon checkIcon = new FontIcon("fas-check-circle");
                checkIcon.setIconSize(14);
                checkIcon.setIconColor(javafx.scene.paint.Color.WHITE);
                contributeBtn.setGraphic(checkIcon);
            }
        } else {
            removeBtn.setVisible(true);
            removeBtn.setManaged(true);

            contributeBtn.setVisible(false);
            contributeBtn.setManaged(false);
        }
    }
}
