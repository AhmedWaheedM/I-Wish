package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import models.User;

public class FriendsController {

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private FlowPane friendsFlowPane;

    @FXML
    private FlowPane findFriendsContainer;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label friendsCountLabel;
    
    private List<User> allNonFriends = new ArrayList<>();
    private List<User> allFriends = new ArrayList<>();

    @FXML
    public void initialize() {
        refresh();
    }

    public void refresh() {
        fetchFriends();
        fetchNonFriends();
    }
    
    @FXML
    private void onSearchKeyReleased() {
        String query = searchField.getText().trim().toLowerCase();
        filterNonFriends(query);
    }
    
    private void filterNonFriends(String query) {
        if (findFriendsContainer == null) return;
        
        findFriendsContainer.getChildren().clear();
        
        List<User> filtered = allNonFriends;
        if (query != null && !query.isEmpty()) {
            filtered = allNonFriends.stream()
                .filter(u -> u.getUserName() != null && 
                            u.getUserName().toLowerCase().contains(query))
                .collect(Collectors.toList());
        }
        
        if (filtered.isEmpty()) {
            Label placeholder = new Label(query.isEmpty() ? "No suggestions available." : "No users found matching \"" + query + "\"");
            placeholder.setStyle("-fx-text-fill: #64748b;");
            findFriendsContainer.getChildren().add(placeholder);
        } else {
            for (User u : filtered) {
                findFriendsContainer.getChildren().add(createNonFriendCard(u));
            }
        }
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
                allFriends = friends;
                
                if (friendsCountLabel != null) {
                    friendsCountLabel.setText(friends.size() + " connection" + (friends.size() != 1 ? "s" : ""));
                }
                
                if (friendsFlowPane != null) {
                    friendsFlowPane.getChildren().clear();
                    
                    if (friends.isEmpty()) {
                        Label placeholder = new Label("No friends yet. Search for people to add!");
                        placeholder.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
                        friendsFlowPane.getChildren().add(placeholder);
                    } else {
                        for (User friend : friends) {
                            friendsFlowPane.getChildren().add(createFriendCard(friend));
                        }
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
                allNonFriends = nonFriends;
                
                // Apply current search filter
                String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                filterNonFriends(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createFriendCard(User friend) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setStyle("-fx-padding: 24; -fx-min-width: 220; -fx-pref-width: 240; " +
                      "-fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        // Avatar with user icon
        StackPane avatarContainer = new StackPane();
        avatarContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #a855f7, #9333ea); " +
                                 "-fx-background-radius: 50%; " +
                                 "-fx-min-width: 64; -fx-min-height: 64; -fx-max-width: 64; -fx-max-height: 64;");
        avatarContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        FontIcon userIcon = new FontIcon("fas-user");
        userIcon.setIconSize(28);
        userIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        avatarContainer.getChildren().add(userIcon);

        // Info
        VBox info = new VBox(4);
        info.setAlignment(javafx.geometry.Pos.CENTER);
        
        String username = friend.getUserName() != null ? friend.getUserName() : "Unknown";
        Label name = new Label(username);
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        
        String handleText = "@" + username.toLowerCase().replace(" ", "");
        Label handle = new Label(handleText);
        handle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        info.getChildren().addAll(name, handle);

        // Actions
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        actions.setStyle("-fx-padding: 8 0 0 0;");
        
        Button viewWishlistBtn = new Button("View Wishlist");
        viewWishlistBtn.getStyleClass().add("button-primary");
        viewWishlistBtn.setStyle("-fx-font-size: 12px;");
        viewWishlistBtn.setOnAction(e -> {
            if (dashboardController != null) {
                dashboardController.showFriendWishlist(friend);
            }
        });

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("button-danger");
        removeBtn.setStyle("-fx-font-size: 12px;");
        removeBtn.setOnAction(e -> handleRemoveFriend(friend));
        
        actions.getChildren().addAll(viewWishlistBtn, removeBtn);

        card.getChildren().addAll(avatarContainer, info, actions);
        return card;
    }

    private VBox createNonFriendCard(User user) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setStyle("-fx-padding: 20; -fx-min-width: 140; -fx-pref-width: 160; " +
                      "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        // Avatar with user icon
        StackPane avatarContainer = new StackPane();
        avatarContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #3b82f6, #2563eb); " +
                                 "-fx-background-radius: 50%; " +
                                 "-fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");
        avatarContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        FontIcon userIcon = new FontIcon("fas-user");
        userIcon.setIconSize(20);
        userIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        avatarContainer.getChildren().add(userIcon);

        // Info
        VBox info = new VBox(2);
        info.setAlignment(javafx.geometry.Pos.CENTER);
        
        String username = user.getUserName() != null ? user.getUserName() : "Unknown";
        Label name = new Label(username);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0f172a;");
        
        String handleText = "@" + username.toLowerCase().replace(" ", "");
        Label handle = new Label(handleText);
        handle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        info.getChildren().addAll(name, handle);

        // Add Button
        Button addBtn = new Button("Add Friend");
        addBtn.getStyleClass().add("button-primary");
        addBtn.setStyle("-fx-font-size: 11px; -fx-padding: 6 12;");
        addBtn.setOnAction(e -> handleAddFriend(user));

        card.getChildren().addAll(avatarContainer, info, addBtn);
        return card;
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
                fetchNonFriends();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
