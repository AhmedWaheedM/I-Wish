package clientSide.controllers;

import java.io.IOException;
import java.util.Optional;

import clientSide.appManger.IWishManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML
    private BorderPane mainLayout;

    @FXML
    private SidebarController sidebarController; 

    @FXML
    private RightSidebarController rightSidebarController; // Injected if fx:include is used

    @FXML
    public void initialize() {
        System.out.println("DashboardController initialized.");
        
        // Link sidebar to this dashboard
        if (sidebarController != null) {
            System.out.println("SidebarController injected successfully.");
            sidebarController.setDashboardController(this);
            
            // Initialize sidebar with user info and current balance
            models.User currentUser = clientSide.appManger.IWishManager.getLoggedInUser();
            if (currentUser != null) {
                this.currentBalance = currentUser.getBalance();
                sidebarController.updateUserInfo(currentUser);
            }
        }

        if (rightSidebarController != null) {
             rightSidebarController.setDashboardController(this);
             rightSidebarController.loadActivity();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/friends_view.fxml"));
            Parent view = loader.load();
            
            FriendsController controller = loader.getController();
            controller.setDashboardController(this);
            
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showFriendWishlist(models.User friend) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/friend_wishlist_view.fxml"));
            Parent view = loader.load();
            
            FriendWishlistController controller = loader.getController();
            controller.setDashboardController(this);
            controller.setFriend(friend);
            
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Cache removed to ensure fresh data on every view switch


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

    public void updateUserInfo(models.User user) {
        if (user != null) {
            // Sync local balance state if using it
            this.currentBalance = user.getBalance();
            if (sidebarController != null) {
                sidebarController.updateUserInfo(user);
            }
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
                // Cache clearing removed
                clientSide.appManger.IWishManager.logout();
                IWishManager.switchScene("login", "I-Wish - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadView(String fxml) {
        try {
            // Construct path to components
            String path = "/views/components/" + fxml + ".fxml";
            java.net.URL resource = getClass().getResource(path);
            
            if (resource == null) {
                System.err.println("Could not find view: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            // detailed cache logic removed
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load view: " + fxml);
        }
    }
}
