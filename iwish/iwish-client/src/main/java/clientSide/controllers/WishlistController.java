package clientSide.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import models.WishListItem;
import org.kordamp.ikonli.javafx.FontIcon;

public class WishlistController {

    @FXML
    private GridPane wishlistGrid;

    private List<WishListItem> wishlistItems;

    @FXML
    public void initialize() {
        wishlistItems = new ArrayList<>();

        loadWishlist();
    }
    private void loadWishlist() {
        showLoading("Loading wishlist...");

        new Thread(() -> {
            List<WishListItem> fetched = fetchWishlistItemsFromServer();

            Platform.runLater(() -> {
                wishlistItems = fetched != null ? fetched : new ArrayList<>();
                populateGrid();
            });
        }, "Wishlist-ReloadThread").start();
    }

    private List<WishListItem> fetchWishlistItemsFromServer() {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();

        if (user == null) {
            System.out.println("DEBUG: No user logged in (IWishManager). Cannot fetch wishlist.");
            return new ArrayList<>();
        }

        System.out.println("DEBUG: Fetching wishlist for user: " + user.getUserName() + " (ID: " + user.getUserId() + ")");

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return new ArrayList<>();

            // 1) Get Wishlist for User
            dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest req1 =
                    new dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest(user.getUserId());

            Object response1 = conn.sendAndWait(req1);

            if (!(response1 instanceof models.WishList)) {
                System.out.println("DEBUG: GetWishListByUserIdRequest returned: " +
                        (response1 != null ? response1.getClass().getName() : "null"));
                return new ArrayList<>();
            }

            models.WishList wishlist = (models.WishList) response1;
            System.out.println("DEBUG: Found Wishlist ID: " + wishlist.getWishListId());

            // 2) Get Items for Wishlist
            dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest req2 =
                    new dtos.requestDtos.wishListItemHandler.GetWishListItemsRequest(wishlist.getWishListId());

            System.out.println("DEBUG: before send and wait " );

            Object response2 = conn.sendAndWait(req2);

            if (response2 instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                List<WishListItem> list = (List<WishListItem>) response2;
                System.out.println("DEBUG: Fetched " + list.size() + " items from server.");
                return list;
            } else {
                System.out.println("DEBUG: GetWishListItemsRequest returned unexpected type: " +
                        (response2 != null ? response2.getClass().getName() : "null"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching wishlist data: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    private void showLoading(String text) {
        if (wishlistGrid == null) return;

        wishlistGrid.getChildren().clear();
        Label loading = new Label(text);
        loading.getStyleClass().add("loading-label");
        wishlistGrid.add(loading, 0, 0);
    }

    private void populateGrid() {
        if (wishlistGrid == null) return;

        wishlistGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (WishListItem item : new ArrayList<>(wishlistItems)) { // copy to avoid concurrent issues
                System.out.println("here " );
            try {
                System.out.println("DEBUG: Adding item to grid: " + item.getItem().getName());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/item_card.fxml"));
                VBox card = loader.load();

                ItemCardController cardController = loader.getController();
                cardController.setData(item);

                cardController.setOnRemove(() -> {
                    clientSide.helpers.MessageDisplayer.showConfirmation(
                        "Remove " + item.getItem().getName() + "?",
                        "Are you sure you want to remove this item from your wishlist?",
                        () -> {
                            // Do remove request in background thread (NO freeze)
                            new Thread(() -> {
                                try {
                                    clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
                                    if (conn == null) return;

                                    dtos.requestDtos.wishListItemHandler.RemoveWishListItemRequest req =
                                            new dtos.requestDtos.wishListItemHandler.RemoveWishListItemRequest(
                                                    item.getWishListId(),
                                                    item.getItem().getItemId()
                                            );

                                    Object response = conn.sendAndWait(req);

                                    Platform.runLater(() -> {
                                        if (response instanceof Boolean && (Boolean) response) {
                                            loadWishlist(); 
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, "Wishlist-RemoveThread").start();
                        }
                    );
                });

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

        // Show empty state if no items
        if (wishlistItems.isEmpty()) {
            VBox emptyState = createEmptyState();
            wishlistGrid.add(emptyState, 0, 0, 3, 1); // Span 3 columns
        }
    }
    
    private VBox createEmptyState() {
        VBox container = new VBox(16);
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("empty-state-container");
        container.setPrefWidth(600);
        
        // Icon container
        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("empty-state-icon-container");
        iconContainer.setAlignment(Pos.CENTER);
        
        FontIcon icon = new FontIcon("fas-gift");
        icon.setIconSize(36);
        icon.setIconColor(Color.web("#94a3b8"));
        iconContainer.getChildren().add(icon);
        
        // Title
        Label title = new Label("Your wishlist is empty");
        title.getStyleClass().add("empty-state-title");
        
        // Message
        Label message = new Label("Add items from the Item Marketplace\nto start building your wishlist!");
        message.getStyleClass().add("empty-state-message");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        
        container.getChildren().addAll(iconContainer, title, message);
        return container;
    }
}
