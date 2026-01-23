package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WalletController {

    @FXML
    private Label currentBalanceLabel;

    @FXML
    private TextField amountField;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setBalance(double balance) {
        if (currentBalanceLabel != null) {
            currentBalanceLabel.setText(String.format("$%.2f", balance));
        }
    }

    @FXML
    public void onAddFundsClicked() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                 System.out.println("Invalid amount");
                 return;
            }

            models.User user = clientSide.ClientSession.getInstance().getCurrentUser();
            if (user == null) {
                System.out.println("No user logged in.");
                return;
            }

            // Send Update Request
            dtos.requestDtos.userHandler.UpdateBalanceRequest request = 
                new dtos.requestDtos.userHandler.UpdateBalanceRequest(user.getUserId(), amount, '+');
            
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn != null) {
                Object response = conn.sendAndWait(request);
                if (response instanceof Boolean && (Boolean) response) {
                    // Success: Update local session and UI
                    double newBalance = user.getBalance() + amount;
                    user.setBalance(newBalance); // Update session
                    
                    if (dashboardController != null) {
                        dashboardController.updateWalletBalance(amount); // Update UI
                    }
                    closeModal();
                } else {
                     System.out.println("Balance update failed on server.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancelClicked() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }
}
