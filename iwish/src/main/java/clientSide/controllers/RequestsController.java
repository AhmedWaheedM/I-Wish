package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.List;
import models.User;

public class RequestsController {

    @FXML
    private VBox requestsListContainer;

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
                
                requestsListContainer.getChildren().clear();
                if (requests.isEmpty()) {
                     Label placeholder = new Label("No pending friend requests.");
                     placeholder.getStyleClass().add("text-muted");
                     requestsListContainer.getChildren().add(placeholder);
                } else {
                    for (User requester : requests) {
                        requestsListContainer.getChildren().add(createRequestItem(requester));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createRequestItem(User requester) {
        HBox item = new HBox(16);
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        item.getStyleClass().add("card");
        item.setStyle("-fx-padding: 16;");

        // Avatar
        Region avatar = new Region();
        avatar.setStyle("-fx-background-color: linear-gradient(to bottom right, #facc15, #ca8a04); -fx-background-radius: 50%; -fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");

        // Info
        VBox info = new VBox(4);
        String username = requester.getUserName() != null ? requester.getUserName() : "Unknown";
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
        HBox actions = new HBox(8);
        Button acceptBtn = new Button("Accept");
        acceptBtn.getStyleClass().add("button-primary");
        acceptBtn.setStyle("-fx-background-color: #22c55e;"); // Green
        acceptBtn.setOnAction(e -> handleAccept(requester));

        Button rejectBtn = new Button("Reject");
        rejectBtn.getStyleClass().add("button-danger");
        rejectBtn.setOnAction(e -> handleReject(requester));

        actions.getChildren().addAll(acceptBtn, rejectBtn);

        item.getChildren().addAll(avatar, info, spacer, actions);
        return item;
    }

    private void handleAccept(User requester) {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;
        
        try {
            // AddFriendRequest handles the accept logic? 
            // Wait, AddFriendRequest usually INITIATES. 
            // Is there an AcceptFriendRequest? 
            // Checking RequestRouter: AddFriendRequest calls friendsHandler.addFriend(u1, u2)
            // If it's already pending, does addFriend confirm it?
            // Usually yes, confirm logic is often just "insert" or "update status".
            // Let's assume AddFriendRequest is reused for acceptance or creates the link.
            // Wait, logic check: if Request exists, 'AddFriend' might mean 'Confirm'.
            
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
