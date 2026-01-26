package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.List;
import models.User;

public class FriendsController {

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private VBox friendsListContainer;

    @FXML
    private HBox findFriendsContainer;

    @FXML
    public void initialize() {
        refresh(); // Call refresh on init
    }

    public void refresh() {
        fetchFriends();
        fetchNonFriends();
    }

    private void fetchFriends() {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return;

            dtos.requestDtos.friendsHandler.GetFriendsRequest req = 
                new dtos.requestDtos.friendsHandler.GetFriendsRequest(user.getUserId());
            
            Object response = conn.sendAndWait(req);
            if (response instanceof List) {
                @SuppressWarnings("unchecked")
                List<User> friends = (List<User>) response;
                
                friendsListContainer.getChildren().clear();
                if (friends.isEmpty()) {
                     Label placeholder = new Label("No friends yet. Search for people to add!");
                     placeholder.getStyleClass().add("text-muted");
                     friendsListContainer.getChildren().add(placeholder);
                } else {
                    for (User friend : friends) {
                        friendsListContainer.getChildren().add(createFriendItem(friend));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchNonFriends() {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return;

            dtos.requestDtos.friendsHandler.GetNonFriendsRequest req = 
                new dtos.requestDtos.friendsHandler.GetNonFriendsRequest(user.getUserId());

            Object response = conn.sendAndWait(req);
            if (response instanceof List) {
                @SuppressWarnings("unchecked")
                List<User> nonFriends = (List<User>) response;

                if (findFriendsContainer != null) {
                    findFriendsContainer.getChildren().clear();
                    if (nonFriends.isEmpty()) {
                         Label placeholder = new Label("No suggestions available.");
                         placeholder.getStyleClass().add("text-muted");
                         findFriendsContainer.getChildren().add(placeholder);
                    } else {
                        for (User u : nonFriends) {
                            findFriendsContainer.getChildren().add(createNonFriendItem(u));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createFriendItem(User friend) {
        HBox item = new HBox(16);
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        item.getStyleClass().add("card");
        item.setStyle("-fx-padding: 16;");

        // Avatar
        Region avatar = new Region();
        avatar.setStyle("-fx-background-color: linear-gradient(to bottom right, #a855f7, #9333ea); -fx-background-radius: 50%; -fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");

        // Info
        VBox info = new VBox(4);
        String username = friend.getUserName() != null ? friend.getUserName() : "Unknown";
        Label name = new Label(username);
        name.getStyleClass().add("text-h3");
        name.setStyle("-fx-font-size: 16px;");
        
        String handleText = "@" + username.toLowerCase().replace(" ", "");
        Label handle = new Label(handleText);
        handle.getStyleClass().add("text-muted");

        info.getChildren().addAll(name, handle);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Actions
        Button viewWishlistBtn = new Button("View Wishlist");
        viewWishlistBtn.getStyleClass().add("button-primary");
        viewWishlistBtn.setStyle("-fx-background-color: #3b82f6;"); // Blue
        viewWishlistBtn.setOnAction(e -> {
            if (dashboardController != null) {
                dashboardController.showFriendWishlist(friend);
            }
        });

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("button-icon");
        removeBtn.setStyle("-fx-text-fill: #ef4444; -fx-background-color: rgba(239, 68, 68, 0.1);");
        removeBtn.setOnAction(e -> handleRemoveFriend(friend));
        
        item.getChildren().addAll(avatar, info, spacer, viewWishlistBtn, removeBtn);
        return item;
    }

    private HBox createNonFriendItem(User user) {
        HBox item = new HBox(12);
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        item.getStyleClass().add("card");
        item.setStyle("-fx-padding: 12; -fx-min-width: 250;");

        // Avatar
        Region avatar = new Region();
        avatar.setStyle("-fx-background-color: linear-gradient(to bottom right, #3b82f6, #2563eb); -fx-background-radius: 50%; -fx-min-width: 40; -fx-min-height: 40; -fx-max-width: 40; -fx-max-height: 40;");

        // Info
        VBox info = new VBox(2);
        String username = user.getUserName() != null ? user.getUserName() : "Unknown";
        Label name = new Label(username);
        name.getStyleClass().add("text-body");
        name.setStyle("-fx-font-weight: bold;");
        
        String handleText = "@" + username.toLowerCase().replace(" ", "");
        Label handle = new Label(handleText);
        handle.getStyleClass().add("text-muted");
        handle.setStyle("-fx-font-size: 11px;");

        info.getChildren().addAll(name, handle);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Add Button
        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("button-small"); // Assuming this style exists or use generic
        addBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
        addBtn.setOnAction(e -> handleAddFriend(user));

        item.getChildren().addAll(avatar, info, spacer, addBtn);
        return item;
    }

    private void handleAddFriend(User targetUser) {
        models.User currentUser = clientSide.appManger.IWishManager.getLoggedInUser();
        if (currentUser == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            dtos.requestDtos.friendsHandler.AddFriendRequest req = 
                new dtos.requestDtos.friendsHandler.AddFriendRequest(currentUser.getUserId(), targetUser.getUserId());
            
            Object response = conn.sendAndWait(req);
            if (response instanceof Boolean && (Boolean) response) {
                // Refresh lists
                fetchNonFriends();
                // Optionally show feedback
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRemoveFriend(User targetUser) {
        models.User currentUser = clientSide.appManger.IWishManager.getLoggedInUser();
        if (currentUser == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            dtos.requestDtos.friendsHandler.RemoveFriendRequest req = 
                new dtos.requestDtos.friendsHandler.RemoveFriendRequest(currentUser.getUserId(), targetUser.getUserId());
            
            Object response = conn.sendAndWait(req);
            if (response instanceof Boolean && (Boolean) response) {
                // Refresh lists
                fetchFriends();
                fetchNonFriends(); // They might appear back in suggestions
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
