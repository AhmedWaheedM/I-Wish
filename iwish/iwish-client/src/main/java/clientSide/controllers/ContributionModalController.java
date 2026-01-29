package clientSide.controllers;

import clientSide.helpers.MessageDisplayer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import models.WishListItem;

public class ContributionModalController {

    @FXML
    private Label itemNameLabel;
    @FXML
    private Label walletBalanceLabel;
    @FXML
    private Label remainingCostLabel;
    @FXML
    private Label maxLabel;
    
    @FXML
    private TextField amountField;
    @FXML
    private Slider amountSlider;
    
    @FXML
    private Button confirmBtn;

    private WishListItem item;
    private User currentUser;
    private Runnable onSuccess;
    
    private double maxContribution;
    private boolean isUpdating = false; // Prevent recursive updates

    public void setData(WishListItem item, User currentUser, Runnable onSuccess) {
        this.item = item;
        this.currentUser = currentUser;
        this.onSuccess = onSuccess;

        if (item.getItem() != null) {
            itemNameLabel.setText("Contribute to " + item.getItem().getName());
        }
        
        walletBalanceLabel.setText(String.format("My Wallet Balance: $%.2f", currentUser.getBalance()));
        
        // Calculate TOTAL price = unit price * quantity
        int qty = Math.max(1, item.getQuantity());
        double unitPrice = item.getItem().getPrice();
        double totalPrice = unitPrice * qty;
        
        double collected = 0;
        if (item.getContributions() != null) {
             for (models.Contribution c : item.getContributions()) {
                 collected += c.getAmount();
             }
        }
        double remaining = totalPrice - collected;
        
        // Ensure remaining isn't negative (fully funded)
        if (remaining < 0) remaining = 0;
        this.maxContribution = remaining;
        
        // Cap by user balance
        if (currentUser.getBalance() < maxContribution) {
            maxContribution = currentUser.getBalance();
        }
        
        remainingCostLabel.setText(String.format("Remaining Cost: $%.2f", remaining));
        
        // Update max label
    if (maxLabel != null) {
        maxLabel.setText(String.format("Max: $%.2f", maxContribution));
        System.out.println("DEBUG: Set maxLabel to: Max: $" + maxContribution);
    } else {
        System.out.println("DEBUG: maxLabel is NULL!");
    }
    
    // Setup Slider
    amountSlider.setMin(0);
    amountSlider.setMax(maxContribution);
    amountSlider.setValue(0);
    amountSlider.setBlockIncrement(1);
    amountSlider.setShowTickMarks(false);
    amountSlider.setShowTickLabels(false);
        
        // Initialize amount field
        amountField.setText("0");
        
        // Slider -> TextField (update on change)
        amountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdating) return;
            isUpdating = true;
            amountField.setText(String.format("%.2f", newVal.doubleValue()));
            isUpdating = false;
        });
        
        // TextField -> Slider (update only when valid number entered)
        amountField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                // Lost focus - validate and update
                validateAndUpdateAmount();
            }
        });
        
        // Also update on Enter key
        amountField.setOnAction(e -> validateAndUpdateAmount());
    }
    
    private void validateAndUpdateAmount() {
        if (isUpdating) return;
        
        String text = amountField.getText().trim();
        try {
            double val = Double.parseDouble(text);
            
            // Clamp between 0 and max
            if (val < 0) val = 0;
            if (val > maxContribution) val = maxContribution;
            
            isUpdating = true;
            amountField.setText(String.format("%.2f", val));
            amountSlider.setValue(val);
            isUpdating = false;
        } catch (NumberFormatException e) {
            // Reset to slider value
            isUpdating = true;
            amountField.setText(String.format("%.2f", amountSlider.getValue()));
            isUpdating = false;
        }
    }

    @FXML
    private void handleConfirm() {
        try {
            String text = amountField.getText().trim();
            double amount = Double.parseDouble(text);
            
            if (amount <= 0) {
                MessageDisplayer.showError("Please enter a valid contribution amount.", "Invalid Amount");
                return;
            }
            
            if (amount > maxContribution) {
                amount = maxContribution;
            }
            
            dtos.requestDtos.contributionHandler.AddContributionRequest req = 
                new dtos.requestDtos.contributionHandler.AddContributionRequest(currentUser.getUserId(), item.getWishListId(), item.getRecId(), amount);
            
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            Object response = conn.sendAndWait(req);
            MessageDisplayer.showContributionSuccess(item.getItem().getName(), amount);
            if (response instanceof Boolean && (Boolean) response) {
                // Success - update local user balance
                currentUser.setBalance(currentUser.getBalance() - amount);
                
                if (onSuccess != null) onSuccess.run();
                
                close();
            } else {
                System.out.println("Contribution failed on server.");
            }
        } catch (NumberFormatException e) {
            MessageDisplayer.showError("Please enter a valid number.", "Invalid Input");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void close() {
        Stage stage = (Stage) confirmBtn.getScene().getWindow();
        stage.close();
    }
}
