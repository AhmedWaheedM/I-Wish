package clientSide.controllers;

import models.User;
import models.WishListItem;
import clientSide.controllers.ItemCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendWishlistController {

    @FXML
    private Label friendNameLabel;

    @FXML
    private GridPane wishlistGrid;

    private User friend;
    private List<WishListItem> wishlistItems;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setFriend(User friend) {
        this.friend = friend;
        if (friendNameLabel != null) {
            friendNameLabel.setText(friend.getUserName() + "'s Wishlist");
        }
        fetchWishlist();
    }

    private void fetchWishlist() {
        wishlistItems = new ArrayList<>();
        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return;

            // 1. Get Wishlist for Friend
            dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest req1 = 
                new dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest(friend.getUserId());
            
            Object response1 = conn.sendAndWait(req1);
            
            if (response1 instanceof models.WishList) {
                models.WishList wishlist = (models.WishList) response1;
                
                // 2. Get Items
                dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest req2 =
                    new dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest(wishlist.getWishListId());
                
                Object response2 = conn.sendAndWait(req2);
                
                if (response2 instanceof List) {
                    wishlistItems = (List<WishListItem>) response2;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }

        populateGrid();
    }

    private void populateGrid() {
        wishlistGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (WishListItem item : wishlistItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/item_card.fxml"));
                VBox card = loader.load();
                
                ItemCardController cardController = loader.getController();
                cardController.setData(item);
                
                // Set Viewer Mode (Contribute instead of Remove)
                cardController.setViewerMode(true);
                
                cardController.setOnContribute(() -> openContributionModal(item));

                wishlistGrid.add(card, col, row);
                col++;
                if (col == 3) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void openContributionModal(WishListItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/contribution_modal.fxml"));
            Parent view = loader.load();
            
            ContributionModalController controller = loader.getController();
            models.User currentUser = clientSide.appManger.IWishManager.getLoggedInUser();
            
            controller.setData(item, currentUser, () -> {
                // Refresh grid on success to show new progress
                fetchWishlist(); 
                
                // Update wallet via dashboard
                if (dashboardController != null) {
                    // Current user balance is already updated in modal, but we need to notify sidebar.
                    // Option 1: Calculate difference. Modal creates 'AddContributionRequest' with 'amount'.
                    // We can capture 'amount' from modal? Modal controller doesn't expose it easily in callback.
                    // But we know 'currentUser' object is shared and updated in modal.
                    dashboardController.updateUserInfo(currentUser);
                }
            });
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Contribute");
            stage.setScene(new Scene(view));
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack() {
        // Find parent DashboardController and navigate back to Friends list?
        // Or simpler: The user can click "Friends List" in sidebar.
        // But for UX, a back button is nice.
        // Assuming we are inside Dashboard BorderPane, we can't easily "go back" without reference.
        // For now, let the user use the Sidebar.
    }
}
