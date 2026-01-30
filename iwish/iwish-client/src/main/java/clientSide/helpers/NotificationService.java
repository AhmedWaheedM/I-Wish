package clientSide.helpers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service to manage and display notifications in the right sidebar.
 */
public class NotificationService {

    private static NotificationService instance;
    private VBox activityListContainer;
    private Button clearAllButton;
    private final List<NotificationItem> notifications = new ArrayList<>();
    // private static final int MAX_NOTIFICATIONS = 20;

    // Notification types with colors and icons
    public enum NotificationType {
        CONTRIBUTION("#22c55e", "#dcfce7", "fas-dollar-sign"),
        FRIEND_REQUEST("#3b82f6", "#dbeafe", "fas-user-plus"),
        FUNDING_MILESTONE("#22c55e", "#dcfce7", "fas-chart-line"),
        WISHLIST_UPDATE("#f97316", "#ffedd5", "fas-plus-circle"),
        FRIEND_ACCEPTED("#a855f7", "#f3e8ff", "fas-user-check"),
        FRIEND_REMOVED("#ef4444", "#fee2e2", "fas-user-minus"),
        INFO("#6b7280", "#f3f4f6", "fas-bell");

        private final String color;
        private final String bgColor;
        private final String icon;

        NotificationType(String color, String bgColor, String icon) {
            this.color = color;
            this.bgColor = bgColor;
            this.icon = icon;
        }

        public String getColor() { return color; }
        public String getBgColor() { return bgColor; }
        public String getIcon() { return icon; }
    }

    public static class NotificationItem {
        public String id;
        public int dbId; // Database ID for deletion
        public String title;
        public String description;
        public NotificationType type;
        public LocalDateTime timestamp;

        public NotificationItem(String title, String description, NotificationType type) {
            this.id = UUID.randomUUID().toString();
            this.dbId = -1;
            this.title = title;
            this.description = description;
            this.type = type;
            this.timestamp = LocalDateTime.now();
        }
        
        public NotificationItem(int dbId, String title, String description, NotificationType type, Timestamp createdAt) {
            this.id = UUID.randomUUID().toString();
            this.dbId = dbId;
            this.title = title;
            this.description = description;
            this.type = type;
            this.timestamp = createdAt != null ? createdAt.toLocalDateTime() : LocalDateTime.now();
        }
    }

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void setActivityListContainer(VBox container) {
        this.activityListContainer = container;
        refreshDisplay();
    }

    public void setClearAllButton(Button button) {
        this.clearAllButton = button;
        if (button != null) {
            button.setOnAction(e -> clearAllNotifications());
            updateClearAllVisibility();
        }
    }

    /**
     * Load recent UNREAD notifications from the database with proper timestamps.
     * This should be called when the right sidebar initializes.
     */
    public void loadDatabaseNotifications() {
        new Thread(() -> {
            try {
                models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
                if (user == null) return;
                
                // Only fetch unread notifications for the right sidebar
                Object response = clientSide.appManger.IWishManager.getClient().sendAndWait(
                    new dtos.requestDtos.notificationHandler.GetUnreadNotificationsRequest(user.getUserId()));
                
                if (response instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<models.Notification> dbNotifications = (List<models.Notification>) response;
                    
                    Platform.runLater(() -> {
                        notifications.clear();
                        
                        int count = 0;
                        for (models.Notification n : dbNotifications) {
                            // Filter out read notifications (client-side safeguard)
                            if (n.isRead()) continue;
                            
                            // if (count >= MAX_NOTIFICATIONS) break;

                            NotificationType type = determineNotificationType(n.getTitle(), n.getBody());
                            NotificationItem item = new NotificationItem(
                                n.getNotificationId(),
                                n.getTitle(),
                                n.getBody(),
                                type,
                                n.getCreatedAt()
                            );
                            notifications.add(item);
                            count++;
                        }
                        
                        refreshDisplay();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "load-db-notifications").start();
    }
    
    /**
     * Request a refresh of the notifications from the database.
     */
    public void requestRefresh() {
        loadDatabaseNotifications();
    }
    
    private NotificationType determineNotificationType(String title, String body) {
        String combined = (title + " " + body).toLowerCase();
        if (combined.contains("contribut")) return NotificationType.CONTRIBUTION;
        if (combined.contains("friend request")) return NotificationType.FRIEND_REQUEST;
        if (combined.contains("accepted") || combined.contains("now friends")) return NotificationType.FRIEND_ACCEPTED;
        if (combined.contains("removed") || combined.contains("unfriend")) return NotificationType.FRIEND_REMOVED;
        if (combined.contains("funded") || combined.contains("milestone") || combined.contains("reached")) return NotificationType.FUNDING_MILESTONE;
        return NotificationType.INFO;
    }

    public void addNotification(String title, String description, NotificationType type) {
        // Check for duplicates - don't add if same title and description exists
        boolean isDuplicate = notifications.stream()
            .anyMatch(n -> n.title.equals(title) && n.description.equals(description));
        
        if (isDuplicate) {
            return; // Skip duplicate notification
        }
        
        NotificationItem item = new NotificationItem(title, description, type);
        notifications.add(0, item);
        
        // if (notifications.size() > MAX_NOTIFICATIONS) {
        //     notifications.remove(notifications.size() - 1);
        // }

        Platform.runLater(() -> {
            if (activityListContainer == null) return;
            
            // Remove placeholder if present
            activityListContainer.getChildren().removeIf(node -> 
                node instanceof Label && ((Label) node).getText().equals("No recent activity"));
            
            HBox notificationView = createNotificationView(item);
            
            // Add with animation
            notificationView.setOpacity(0);
            notificationView.setTranslateX(20);
            activityListContainer.getChildren().add(0, notificationView);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), notificationView);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), notificationView);
            slide.setFromX(20);
            slide.setToX(0);
            
            new ParallelTransition(fade, slide).play();
            
            // Remove oldest if too many displayed
            // if (activityListContainer.getChildren().size() > MAX_NOTIFICATIONS) {
            //     activityListContainer.getChildren().remove(activityListContainer.getChildren().size() - 1);
            // }
            
            updateClearAllVisibility();
            updateClearAllVisibility();
        });
    }

    private void onDismissClicked(NotificationItem item) {
        // Handle database updates (Mark Read ONLY)
        System.out.println("DEBUG: Dismissing notification ID: " + item.id + ", DB ID: " + item.dbId);
        new Thread(() -> {
            try {
                if (item.dbId != -1) {
                    System.out.println("DEBUG: Sending MarkRead request for DB ID: " + item.dbId);
                    // 1. Mark as Read
                    Object readRes = clientSide.appManger.IWishManager.getClient().sendAndWait(
                        new dtos.requestDtos.notificationHandler.MarkNotificationAsReadRequest(item.dbId));
                    System.out.println("DEBUG: MarkRead response: " + readRes);
                        
                    // NOTE: We no longer clear (soft delete) from here, just mark as read.
                } else {
                    System.out.println("DEBUG: Skipping DB requests because DB ID is -1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "dismiss-sidebar-action").start();

        // Perform UI animation
        dismissNotification(item.id);
    }
    
    /**
     * Dismiss a single notification by ID (UI only).
     */
    public void dismissNotification(String notificationId) {
        Platform.runLater(() -> {
            if (activityListContainer == null) {
                notifications.removeIf(n -> n.id.equals(notificationId));
                return;
            }
            
            // Find the notification view by ID
            javafx.scene.Node nodeToRemove = null;
            for (javafx.scene.Node node : activityListContainer.getChildren()) {
                if (node.getUserData() != null && node.getUserData().equals(notificationId)) {
                    nodeToRemove = node;
                    break;
                }
            }
            
            if (nodeToRemove != null) {
                final javafx.scene.Node finalNode = nodeToRemove;
                
                // Animate fade out and slide right
                FadeTransition fade = new FadeTransition(Duration.millis(300), finalNode);
                fade.setFromValue(1);
                fade.setToValue(0);
                
                TranslateTransition slide = new TranslateTransition(Duration.millis(300), finalNode);
                slide.setFromX(0);
                slide.setToX(50);
                
                ParallelTransition animation = new ParallelTransition(fade, slide);
                animation.setOnFinished(e -> {
                    notifications.removeIf(n -> n.id.equals(notificationId));
                    activityListContainer.getChildren().remove(finalNode);
                    updateClearAllVisibility();
                    
                    // Show placeholder if empty
                    if (notifications.isEmpty()) {
                        refreshDisplay();
                    }
                });
                animation.play();
            } else {
                notifications.removeIf(n -> n.id.equals(notificationId));
                refreshDisplay();
                updateClearAllVisibility();
            }
        });
    }
    
    /**
     * Dismiss a notification by database ID.
     */
    public void dismissNotificationByDbId(int dbId) {
        Platform.runLater(() -> {
            if (activityListContainer == null) {
                notifications.removeIf(n -> n.dbId == dbId);
                return;
            }
            
            // Find the notification by dbId
            NotificationItem itemToRemove = notifications.stream()
                .filter(n -> n.dbId == dbId)
                .findFirst()
                .orElse(null);
            
            if (itemToRemove != null) {
                dismissNotification(itemToRemove.id);
            }
        });
    }

    /**
     * Clear all notifications with animation and mark as read on server.
     */
    public void clearAllNotifications() {
        // Send request to server
        new Thread(() -> {
            try {
                models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
                if (user != null) {
                    clientSide.appManger.IWishManager.getClient().sendAndWait(
                        new dtos.requestDtos.notificationHandler.MarkAllNotificationsAsReadRequest(user.getUserId()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "clear-all-sidebar").start();

        notifications.clear();
        Platform.runLater(() -> {
            if (activityListContainer != null) {
                FadeTransition fade = new FadeTransition(Duration.millis(200), activityListContainer);
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.setOnFinished(e -> {
                    refreshDisplay();
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), activityListContainer);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                });
                fade.play();
            }
            updateClearAllVisibility();
        });
    }

    private void updateClearAllVisibility() {
        if (clearAllButton != null) {
            clearAllButton.setVisible(!notifications.isEmpty());
            clearAllButton.setManaged(!notifications.isEmpty());
        }
    }

    public void showContributionNotification(String contributorName, String itemName, double amount) {
        String desc = String.format("%s contributed $%.2f to your %s", contributorName, amount, itemName);
        addNotification("New Contribution", desc, NotificationType.CONTRIBUTION);
    }

    public void showFriendRequestNotification(String senderName) {
        addNotification("Friend Request", senderName + " sent you a friend request", NotificationType.FRIEND_REQUEST);
    }

    public void showFundingMilestoneNotification(String itemName, int percentage) {
        addNotification("Funding Milestone", "Your " + itemName + " reached " + percentage + "% funding!", NotificationType.FUNDING_MILESTONE);
    }

    public void showNewFriendshipNotification(String friendName) {
        addNotification("New Friend", "You are now friends with " + friendName, NotificationType.FRIEND_ACCEPTED);
    }

    public void showSuccessNotification(String title, String description) {
        addNotification(title, description, NotificationType.INFO);
    }

    private void refreshDisplay() {
        if (activityListContainer == null) return;

        activityListContainer.getChildren().clear();

        if (notifications.isEmpty()) {
            Label placeholder = new Label("No recent activity");
            placeholder.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px; -fx-padding: 8 0;");
            activityListContainer.getChildren().add(placeholder);
            updateClearAllVisibility();
            return;
        }

        for (NotificationItem item : notifications) {
            HBox notificationView = createNotificationView(item);
            activityListContainer.getChildren().add(notificationView);
        }
        updateClearAllVisibility();
    }

    private HBox createNotificationView(NotificationItem item) {
        // Main container with colored glow effect
        HBox container = new HBox(0);
        container.setUserData(item.id); // Set ID for finding later
        container.setAlignment(Pos.TOP_LEFT);
        container.getStyleClass().add("sidebar-notif-item");
        container.setStyle(String.format(
            "-fx-effect: dropshadow(gaussian, %s, 8, 0.3, 0, 2);",
            item.type.getColor()
        ));

        // Left colored border strip
        Region leftBorder = new Region();
        leftBorder.setMinWidth(4);
        leftBorder.setMaxWidth(4);
        leftBorder.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 8 0 0 8;",
            item.type.getColor()
        ));

        // Content area
        HBox content = new HBox(8);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(10, 8, 10, 10));
        HBox.setHgrow(content, Priority.ALWAYS);

        // Icon
        FontIcon icon = new FontIcon(item.type.getIcon());
        icon.setIconColor(Color.web(item.type.getColor()));
        icon.setIconSize(14);

        // Text container
        VBox textContainer = new VBox(2);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        Label descLabel = new Label(item.description);
        descLabel.setWrapText(true);
        descLabel.getStyleClass().add("sidebar-notif-text");
        descLabel.setMaxWidth(140);

        Label timeLabel = new Label(formatTimestamp(item.timestamp));
        timeLabel.getStyleClass().add("sidebar-notif-time");

        textContainer.getChildren().addAll(descLabel, timeLabel);

        // Close button
        Button closeBtn = new Button();
        FontIcon closeIcon = new FontIcon("fas-times");
        closeIcon.setIconColor(Color.web("#9ca3af"));
        closeIcon.setIconSize(10);
        closeBtn.setGraphic(closeIcon);
        closeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-cursor: hand;");
        closeBtn.setOnMouseEntered(e -> closeIcon.setIconColor(Color.web("#ef4444")));
        closeBtn.setOnMouseExited(e -> closeIcon.setIconColor(Color.web("#9ca3af")));
        closeBtn.setOnAction(e -> onDismissClicked(item));

        content.getChildren().addAll(icon, textContainer, closeBtn);
        container.getChildren().addAll(leftBorder, content);

        return container;
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(timestamp, now).toMinutes();
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + (hours == 1 ? " hour ago" : " hours ago");
        
        long days = hours / 24;
        if (days < 7) return days + (days == 1 ? " day ago" : " days ago");
        
        return timestamp.format(DateTimeFormatter.ofPattern("MMM d"));
    }
}
