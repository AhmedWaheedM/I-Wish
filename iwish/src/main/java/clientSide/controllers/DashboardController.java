package clientSide.controllers;

import clientSide.appManger.IWishManager;
import clientSide.controllers.WalletController;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Optional;

public class DashboardController {

    @FXML
    private BorderPane mainLayout;

    @FXML
    private SidebarController sidebarController; // Injected by fx:include if fx:id="sidebar" is used

    @FXML
    public void initialize() {
        System.out.println("DashboardController initialized.");
        // Link sidebar to this dashboard
        if (sidebarController != null) {
            System.out.println("SidebarController injected successfully.");
            sidebarController.setDashboardController(this);
            // Initialize sidebar with current balance
            sidebarController.updateBalanceDisplay(currentBalance);
        } else {
            // Check if we can find it via lookup if injection failed (unlikely if fx:id matches)
            // System.err.println("SidebarController NOT injected.");
        }
        
        // Show default view
        showWishlist();
    }

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
        this.sidebarController.setDashboardController(this);
    }

    public void showWishlist() {
        loadView("wishlist_view");
    }

    public void showMarketplace() {
        loadView("item_marketplace");
    }

    public void showFriends() {
        loadView("friends_view");
    }
    
    private java.util.Map<String, Parent> viewCache = new java.util.HashMap<>();

    public void showRequests() {
         loadView("requests_view");
    }
    
    public void showNotifications() {
         loadView("notifications_view");
    }

    private double currentBalance = 500.00; // Mock state

    public void updateWalletBalance(double amountToAdd) {
        System.out.println("DEBUG: updateWalletBalance called with " + amountToAdd);
        currentBalance += amountToAdd;
        if (sidebarController != null) {
            sidebarController.updateBalanceDisplay(currentBalance);
        }
    }

    public void showWallet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/wallet_modal.fxml"));
            Parent view = loader.load();
            
            WalletController walletController = loader.getController();
            walletController.setDashboardController(this);
            walletController.setBalance(currentBalance); // Set current balance
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("My Wallet");
            stage.setScene(new javafx.scene.Scene(view));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be returned to the login screen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Clear cache on logout
                viewCache.clear();
                clientSide.ClientSession.getInstance().logout();
                IWishManager.switchScene("login", "I-Wish - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadView(String fxml) {
        try {
            if (viewCache.containsKey(fxml)) {
                mainLayout.setCenter(viewCache.get(fxml));
                return;
            }

            // Construct path to components
            String path = "/views/components/" + fxml + ".fxml";
            java.net.URL resource = getClass().getResource(path);
            
            if (resource == null) {
                System.err.println("Could not find view: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            viewCache.put(fxml, view);
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load view: " + fxml);
        }
    }
}
