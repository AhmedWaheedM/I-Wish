package clientSide.controllers;

import models.WishListItem;
import models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ContributionModalController {

    @FXML
    private Label itemNameLabel;
    @FXML
    private Label walletBalanceLabel;
    @FXML
    private Label remainingCostLabel;
    
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

    public void setData(WishListItem item, User currentUser, Runnable onSuccess) {
        this.item = item;
        this.currentUser = currentUser;
        this.onSuccess = onSuccess;

        if (item.getItem() != null) {
            itemNameLabel.setText("Contribute to " + item.getItem().getName());
        }
        
        walletBalanceLabel.setText(String.format("My Wallet Balance: $%.2f", currentUser.getBalance()));
        
        double price = item.getItem().getPrice();
        double collected = 0;
        if (item.getContributions() != null) {
             for (models.Contribution c : item.getContributions()) {
                 collected += c.getAmount();
             }
        }
        double remaining = price - collected;
        this.maxContribution = remaining;
        
        // Cap by user balance
        if (currentUser.getBalance() < maxContribution) {
            maxContribution = currentUser.getBalance();
        }
        
        remainingCostLabel.setText(String.format("Remaining Cost: $%.2f", remaining));
        
        // Setup Slider
        amountSlider.setMin(0);
        amountSlider.setMax(maxContribution);
        amountSlider.setValue(0);
        
        // Listeners
        amountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            amountField.setText(String.format("%.2f", newVal.doubleValue()));
        });
        
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
             try {
                 double val = Double.parseDouble(newVal);
                 if (val > maxContribution) {
                     val = maxContribution;
                     amountField.setText(String.format("%.2f", val));
                 }
                 amountSlider.setValue(val);
             } catch (NumberFormatException e) {
                 // ignore
             }
        });
    }

    @FXML
    private void handleConfirm() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) return;
            
            dtos.requestDtos.contributionHandler.AddContributionRequest req = 
                new dtos.requestDtos.contributionHandler.AddContributionRequest(currentUser.getUserId(), item.getWishListId(), amount);
            
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            Object response = conn.sendAndWait(req);
            
            if (response instanceof Boolean && (Boolean) response) {
                // Success
                // Update local user balance mock
                currentUser.setBalance(currentUser.getBalance() - amount);
                
                if (onSuccess != null) onSuccess.run();
                
                close();
            } else {
                System.out.println("Contribution failed on server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void close() {
        Stage stage = (Stage) confirmBtn.getScene().getWindow();
        stage.close();
    }
}
