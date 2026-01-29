package clientSide.controllers;

import java.io.IOException;

import clientSide.appManger.IWishManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        // Create custom styled dialog
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        dialogStage.setTitle("Logout");
        
        // Main container
        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(20);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.setPadding(new javafx.geometry.Insets(32));
        container.setStyle("-fx-background-color: white; -fx-border-color: #e2dbdbff;-fx-background-radius: 12; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
        container.setPrefWidth(380);
        
        // Icon
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.setStyle("-fx-background-color: #fef2f2; -fx-background-radius: 50%;");
        
        org.kordamp.ikonli.javafx.FontIcon logoutIcon = new org.kordamp.ikonli.javafx.FontIcon("fas-power-off");
        logoutIcon.setIconSize(28);
        logoutIcon.setIconColor(javafx.scene.paint.Color.web("#ef4444"));
        iconContainer.getChildren().add(logoutIcon);
        
        // Title
        javafx.scene.control.Label title = new javafx.scene.control.Label("Confirm Logout");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        
        // Message
        javafx.scene.control.Label message = new javafx.scene.control.Label("Are you sure you want to logout?\nYou will be returned to the login screen.");
        message.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-text-alignment: center;");
        message.setWrapText(true);
        message.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Buttons
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(12);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        buttons.setPadding(new javafx.geometry.Insets(8, 0, 0, 0));
        
        javafx.scene.control.Button cancelBtn = new javafx.scene.control.Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; " +
                          "-fx-background-radius: 8; -fx-padding: 10 24; -fx-font-size: 14px; " +
                          "-fx-text-fill: #64748b; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialogStage.close());
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; " +
                          "-fx-background-radius: 8; -fx-padding: 10 24; -fx-font-size: 14px; " +
                          "-fx-text-fill: #475569; -fx-cursor: hand;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8; " +
                          "-fx-background-radius: 8; -fx-padding: 10 24; -fx-font-size: 14px; " +
                          "-fx-text-fill: #64748b; -fx-cursor: hand;"));
        
        javafx.scene.control.Button logoutBtn = new javafx.scene.control.Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 8; -fx-padding: 10 24; " +
                          "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            dialogStage.close();
            try {
                clientSide.appManger.IWishManager.logout();
                IWishManager.switchScene("login", "I-Wish - Login");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 8; -fx-padding: 10 24; " +
                          "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 8; -fx-padding: 10 24; " +
                          "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"));
        
        buttons.getChildren().addAll(cancelBtn, logoutBtn);
        
        container.getChildren().addAll(iconContainer, title, message, buttons);
        
        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);
        
        // Scene with transparent background for rounded corners
        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setScene(scene);
        
        // Get owner window for centering
        javafx.stage.Stage ownerStage = (javafx.stage.Stage) mainLayout.getScene().getWindow();
        if (ownerStage != null) {
            dialogStage.initOwner(ownerStage);
            dialogStage.setOnShown(event -> {
                double ownerX = ownerStage.getX();
                double ownerY = ownerStage.getY();
                double ownerW = ownerStage.getWidth();
                double ownerH = ownerStage.getHeight();
                
                double dialogW = dialogStage.getWidth();
                double dialogH = dialogStage.getHeight();
                
                dialogStage.setX(ownerX + (ownerW / 2) - (dialogW / 2));
                dialogStage.setY(ownerY + (ownerH / 2) - (dialogH / 2));
                
                // Play entrance animation
                javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(
                    javafx.util.Duration.millis(200), container);
                scaleIn.setFromX(0.8);
                scaleIn.setFromY(0.8);
                scaleIn.setToX(1.0);
                scaleIn.setToY(1.0);
                
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(200), container);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                
                javafx.animation.ParallelTransition entrance = new javafx.animation.ParallelTransition(scaleIn, fadeIn);
                entrance.play();
            });
        } else {
            dialogStage.centerOnScreen();
        }
        dialogStage.showAndWait();
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
