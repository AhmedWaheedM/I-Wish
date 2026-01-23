package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class SidebarController {

    @FXML
    private Label walletBalanceLabel;

    @FXML
    private Button walletBtn;

    @FXML
    private Button wishlistBtn; // Need fx:id in FXML
    @FXML
    private Button marketplaceBtn;
    @FXML
    private Button friendsBtn;
    @FXML
    private Button requestsBtn;
    @FXML
    private Button notificationsBtn;
    @FXML
    private Button logoutBtn;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    public void initialize() {
        System.out.println("SidebarController initialized.");
    }
    
    // Actions
    public void onWalletClicked() {
        if (dashboardController != null) dashboardController.showWallet();
    }

    public void onWishlistClicked() {
        setActiveButton(wishlistBtn);
        if (dashboardController != null) dashboardController.showWishlist();
    }
    
    public void onMarketplaceClicked() {
        setActiveButton(marketplaceBtn);
        if (dashboardController != null) dashboardController.showMarketplace();
    }
    
    public void onFriendsClicked() {
        setActiveButton(friendsBtn);
        if (dashboardController != null) dashboardController.showFriends();
    }
    
    public void onRequestsClicked() {
        setActiveButton(requestsBtn);
         if (dashboardController != null) dashboardController.showRequests();
    }
    
    public void onNotificationsClicked() {
        setActiveButton(notificationsBtn);
         if (dashboardController != null) dashboardController.showNotifications();
    }
    
    public void onLogoutClicked() {
         if (dashboardController != null) dashboardController.logout();
    }

    public void updateBalanceDisplay(double newBalance) {
        walletBalanceLabel.setText(String.format("$%.2f", newBalance));
    }

    private void setActiveButton(Button newActiveBtn) {
        // Remove active class from all buttons
        wishlistBtn.getStyleClass().remove("active");
        marketplaceBtn.getStyleClass().remove("active");
        friendsBtn.getStyleClass().remove("active");
        requestsBtn.getStyleClass().remove("active");
        notificationsBtn.getStyleClass().remove("active");

        // Add active class to clicked button
        if (!newActiveBtn.getStyleClass().contains("active")) {
             newActiveBtn.getStyleClass().add("active");
        }
    }
}
