package clientSide.controllers;

import clientSide.helpers.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;
import models.User;

public class RightSidebarController {

    @FXML
    private VBox activityList;
    
    @FXML
    private ScrollPane activityScrollPane;
    
    @FXML
    private Button clearAllBtn;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void initialize() {
        // Register this container with the NotificationService
        NotificationService.getInstance().setActivityListContainer(activityList);
        
        // Register the Clear All button
        NotificationService.getInstance().setClearAllButton(clearAllBtn);
        
        // Load initial activity (like pending friend requests)
        loadActivity();
    }

    public void loadActivity() {
        models.User currentUser = clientSide.appManger.IWishManager.getLoggedInUser();
        if (currentUser == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn != null) {
                // Get Pending Friends (Friend Requests) and add as notifications
                dtos.requestDtos.friendsHandler.GetPendingFriendsRequest req = 
                    new dtos.requestDtos.friendsHandler.GetPendingFriendsRequest(currentUser.getUserId());
                
                Object response = conn.sendAndWait(req);
                if (response instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<User> pending = (List<User>) response;
                    for (User u : pending) {
                        NotificationService.getInstance().showFriendRequestNotification(u.getUserName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Refresh the activity list.
     */
    public void refresh() {
        loadActivity();
    }
}
