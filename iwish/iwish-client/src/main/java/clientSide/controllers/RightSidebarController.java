package clientSide.controllers;

import clientSide.helpers.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

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
        
        // Load initial activity from database
        loadActivity();
    }

    public void loadActivity() {
        models.User currentUser = clientSide.appManger.IWishManager.getLoggedInUser();
        if (currentUser == null) return;

        // Load notifications from database with proper timestamps
        NotificationService.getInstance().loadDatabaseNotifications();
    }
    
    /**
     * Refresh the activity list.
     */
    public void refresh() {
        loadActivity();
    }
}
