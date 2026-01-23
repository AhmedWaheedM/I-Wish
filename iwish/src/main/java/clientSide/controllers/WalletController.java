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
            if (amount > 0 && dashboardController != null) {
                dashboardController.updateWalletBalance(amount);
                closeModal();
            }
        } catch (NumberFormatException e) {
            // Invalid input, ignore or show error
            System.out.println("Invalid amount");
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
