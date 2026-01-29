package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class SidebarController {

    @FXML
    private Label userNameLabel;
    @FXML
    private Label userHandleLabel;
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
        if (walletBalanceLabel != null)
            walletBalanceLabel.setText(String.format("$%.2f", newBalance));
    }

    public void updateUserInfo(models.User user) {
        if (user != null) {
            if (userNameLabel != null) userNameLabel.setText(user.getUserName());
            if (userHandleLabel != null) userHandleLabel.setText("@" + user.getUserName().toLowerCase().replace(" ", ""));
            if (walletBalanceLabel != null) walletBalanceLabel.setText(String.format("$%.2f", user.getBalance()));
        }
    }

    private void setActiveButton(Button newActiveBtn) {
        // Find the currently active button and animate it out
        Button[] allButtons = {wishlistBtn, marketplaceBtn, friendsBtn, requestsBtn, notificationsBtn};
        
        for (Button btn : allButtons) {
            if (btn.getStyleClass().contains("active") && btn != newActiveBtn) {
                // Animate out the old button
                javafx.animation.ScaleTransition scaleOut = new javafx.animation.ScaleTransition(
                    javafx.util.Duration.millis(250), btn);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.play();
            }
            btn.getStyleClass().remove("active");
        }

        // Add active class and animate the new button
        if (!newActiveBtn.getStyleClass().contains("active")) {
            newActiveBtn.getStyleClass().add("active");
            
            // Scale animation - subtle pop effect
            javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(150), newActiveBtn);
            scaleIn.setFromX(0.90);
            scaleIn.setFromY(0.90);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            
            // Fade animation
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(150), newActiveBtn);
            fadeIn.setFromValue(0.7);
            fadeIn.setToValue(1.0);
            
            javafx.animation.ParallelTransition entrance = new javafx.animation.ParallelTransition(scaleIn, fadeIn);
            entrance.play();
        }
    }
}
