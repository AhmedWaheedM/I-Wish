package clientSide.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.WishListItem;

public class WishlistController {

    @FXML
    private GridPane wishlistGrid;

    private List<WishListItem> wishlistItems;

    @FXML
    public void initialize() {
        wishlistItems = new ArrayList<>();

        showLoading("Loading wishlist...");

        new Thread(() -> {
                System.out.println("before fetch ");
            List<WishListItem> fetched = fetchWishlistItemsFromServer();

            Platform.runLater(() -> {
                wishlistItems = fetched != null ? fetched : new ArrayList<>();
                populateGrid();
            });

        }, "Wishlist-LoadThread").start();
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
        loading.setStyle("-fx-font-size: 16px; -fx-padding: 20;");
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Remove Item");
                    alert.setHeaderText("Remove " + item.getItem().getName() + "?");
                    alert.setContentText("Are you sure you want to remove this item from your wishlist?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {

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

                                        if (item.getQuantity() > 1) {
                                            item.setQuantity(item.getQuantity() - 1);
                                        } else {
                                            wishlistItems.remove(item);
                                        }
                                        populateGrid();

                                    } else {
                                        System.out.println("Failed to remove item CLIENT SIDE.");
                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, "Wishlist-RemoveThread").start();
                    }
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

        // Optional: show empty state
        if (wishlistItems.isEmpty()) {
            Label empty = new Label("No items in your wishlist yet.");
            empty.setStyle("-fx-font-size: 14px; -fx-padding: 20;");
            wishlistGrid.add(empty, 0, 0);
        }
    }
}
