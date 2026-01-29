package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.List;
import models.User;

public class RequestsController {

    @FXML
    private FlowPane requestsContainer;
    
    @FXML
    private Label countLabel;

    @FXML
    public void initialize() {
        fetchRequests();
    }

    private void fetchRequests() {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return;

            dtos.requestDtos.friendsHandler.GetPendingFriendsRequest req = 
                new dtos.requestDtos.friendsHandler.GetPendingFriendsRequest(user.getUserId());
            
            Object response = conn.sendAndWait(req);
            if (response instanceof List) {
                @SuppressWarnings("unchecked")
                List<User> requests = (List<User>) response;
                
                requestsContainer.getChildren().clear();
                
                // Update count label
                if (countLabel != null) {
                    countLabel.setText(requests.size() + " pending");
                }
                
                if (requests.isEmpty()) {
                    VBox emptyState = createEmptyState();
                    requestsContainer.getChildren().add(emptyState);
                } else {
                    for (User requester : requests) {
                        requestsContainer.getChildren().add(createRequestCard(requester));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private VBox createEmptyState() {
        VBox empty = new VBox(12);
        empty.setAlignment(Pos.CENTER);
        empty.setPrefWidth(600);
        empty.setPadding(new Insets(48));
        
        FontIcon icon = new FontIcon("fas-user-friends");
        icon.setIconSize(48);
        icon.setIconColor(Color.web("#94a3b8"));
        
        Label message = new Label("No pending friend requests");
        message.getStyleClass().add("request-empty-message");
        
        Label subMessage = new Label("When someone sends you a friend request, it will appear here");
        subMessage.getStyleClass().add("request-empty-sub");
        
        empty.getChildren().addAll(icon, message, subMessage);
        return empty;
    }

    private VBox createRequestCard(User requester) {
        VBox card = new VBox(16);
        card.setPrefWidth(280);
        card.setMinWidth(260);
        card.setMaxWidth(300);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("request-card");

        // Avatar with user icon
        StackPane avatarContainer = new StackPane();
        avatarContainer.setMinSize(72, 72);
        avatarContainer.setMaxSize(72, 72);
        avatarContainer.getStyleClass().add("request-avatar");
        avatarContainer.setAlignment(Pos.CENTER);
        
        FontIcon userIcon = new FontIcon("fas-user");
        userIcon.setIconSize(32);
        userIcon.setIconColor(Color.WHITE);
        avatarContainer.getChildren().add(userIcon);

        // Info
        VBox info = new VBox(4);
        info.setAlignment(Pos.CENTER);
        
        String username = requester.getUserName() != null ? requester.getUserName() : "Unknown";
        Label name = new Label(username);
        name.getStyleClass().add("request-name");
        
        String handleText = "@" + username.toLowerCase().replace(" ", "");
        Label handle = new Label(handleText);
        handle.getStyleClass().add("request-handle");
        
        Label requestLabel = new Label("wants to be your friend");
        requestLabel.getStyleClass().add("request-tagline");

        info.getChildren().addAll(name, handle, requestLabel);

        // Actions
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(8, 0, 0, 0));
        
        Button acceptBtn = new Button("Accept");
        acceptBtn.getStyleClass().addAll("button-primary", "btn-accept");
        acceptBtn.setPrefWidth(100);
        acceptBtn.setOnAction(e -> handleAccept(requester));
        
        // Add icon to accept button
        FontIcon checkIcon = new FontIcon("fas-check");
        checkIcon.setIconSize(12);
        checkIcon.setIconColor(Color.WHITE);
        acceptBtn.setGraphic(checkIcon);

        Button rejectBtn = new Button("Decline");
        rejectBtn.getStyleClass().add("button-danger");
        rejectBtn.setPrefWidth(100);
        rejectBtn.setOnAction(e -> handleReject(requester));
        
        // Add icon to reject button - WHITE color to match button background
        FontIcon timesIcon = new FontIcon("fas-times");
        timesIcon.setIconSize(12);
        timesIcon.setIconColor(Color.WHITE);
        rejectBtn.setGraphic(timesIcon);

        actions.getChildren().addAll(acceptBtn, rejectBtn);

        card.getChildren().addAll(avatarContainer, info, actions);
        return card;
    }

    private void handleAccept(User requester) {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;
        
        try {
            dtos.requestDtos.friendsHandler.AcceptFriendRequest req = 
                new dtos.requestDtos.friendsHandler.AcceptFriendRequest(requester.getUserId(), user.getUserId());
            
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            conn.sendAndWait(req);
            
            // Refresh
            fetchRequests();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleReject(User requester) {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;

        try {
            dtos.requestDtos.friendsHandler.RejectFriendRequest req = 
                new dtos.requestDtos.friendsHandler.RejectFriendRequest(user.getUserId(), requester.getUserId());
            
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            conn.sendAndWait(req);
            
            // Refresh
            fetchRequests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
