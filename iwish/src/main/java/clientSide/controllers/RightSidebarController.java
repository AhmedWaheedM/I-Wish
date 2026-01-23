package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import models.User;

public class RightSidebarController {

    @FXML
    private VBox activityList;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void initialize() {
        // loadActivity();
    }

    public void loadActivity() {
        activityList.getChildren().clear();
        
        models.User currentUser = clientSide.ClientSession.getInstance().getCurrentUser();
        if (currentUser == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn != null) {
                // 1. Get Pending Friends (Friend Requests)
                dtos.requestDtos.friendsHandler.GetPendingFriendsRequest req = 
                    new dtos.requestDtos.friendsHandler.GetPendingFriendsRequest(currentUser.getUserId());
                
                Object response = conn.sendAndWait(req);
                if (response instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<User> pending = (List<User>) response;
                    for (User u : pending) {
                        addActivityItem("New Friend Request", u.getUserName() + " sent a request", "fas-user-plus", "#60a5fa");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // If empty, show placeholder?
        if (activityList.getChildren().isEmpty()) {
            // Optional: add placeholder
        }
    }

    private void addActivityItem(String title, String description, String icon, String color) {
        HBox item = new HBox(10);
        item.getStyleClass().add("card");
        item.setStyle("-fx-background-color: #1e293b; -fx-padding: 12;");
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconColor(javafx.scene.paint.Color.web(color));
        fontIcon.setIconSize(16);

        VBox textContainer = new VBox();
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("text-sm");
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label descLbl = new Label(description);
        descLbl.getStyleClass().add("text-muted");
        descLbl.setStyle("-fx-font-size: 12px;");

        textContainer.getChildren().addAll(titleLbl, descLbl);
        item.getChildren().addAll(fontIcon, textContainer);

        activityList.getChildren().add(item);
    }
}
