package clientSide.controllers;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import clientSide.appManger.IWishManager;
import dtos.requestDtos.notificationHandler.GetNotificationsRequest;
import dtos.requestDtos.notificationHandler.MarkAllNotificationsAsReadRequest;
import dtos.requestDtos.notificationHandler.MarkNotificationAsReadRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class NotificationsController {

    // Notification type for styling
    public enum NotificationType {
        CONTRIBUTION("#22c55e", "fas-dollar-sign"),
        FRIEND_REQUEST("#3b82f6", "fas-user-plus"),
        FUNDING_MILESTONE("#22c55e", "fas-chart-line"),
        FRIEND_ACCEPTED("#a855f7", "fas-user-check"),
        FRIEND_REMOVED("#ef4444", "fas-user-minus"),
        INFO("#6b7280", "fas-bell");

        private final String color;
        private final String icon;

        NotificationType(String color, String icon) {
            this.color = color;
            this.icon = icon;
        }

        public String getColor() { return color; }
        public String getIcon() { return icon; }
    }

    public static class AppNotification {
        private final int id;
        private final String title;
        private final String body;
        private boolean read;
        private NotificationType type;
        private Timestamp createdAt;

        public AppNotification(int id, String title, String body, boolean read, Timestamp createdAt) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.read = read;
            this.type = determineType(title, body);
            this.createdAt = createdAt;
        }

        private NotificationType determineType(String title, String body) {
            String combined = (title + " " + body).toLowerCase();
            if (combined.contains("contribut")) return NotificationType.CONTRIBUTION;
            if (combined.contains("friend request")) return NotificationType.FRIEND_REQUEST;
            if (combined.contains("accepted") || combined.contains("now friends")) return NotificationType.FRIEND_ACCEPTED;
            if (combined.contains("removed") || combined.contains("unfriend")) return NotificationType.FRIEND_REMOVED;
            if (combined.contains("funded") || combined.contains("milestone") || combined.contains("reached")) return NotificationType.FUNDING_MILESTONE;
            return NotificationType.INFO;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getBody() { return body; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
        public NotificationType getType() { return type; }
        public Timestamp getCreatedAt() { return createdAt; }
    }

    @FXML private FlowPane notificationsGrid;
    @FXML private Label emptyLabel;
    @FXML private Label countLabel;

    private final List<AppNotification> items = new ArrayList<>();
    private volatile boolean loading = false;

    @FXML
    private void initialize() {
        refreshAsync();
    }

    @FXML
    private void onRefresh() {
        refreshAsync();
        clientSide.helpers.NotificationService.getInstance().requestRefresh();
    }

    @FXML
    private void onMarkAllRead() {
        if (items.isEmpty()) return;

        items.forEach(n -> n.setRead(true));
        refreshGrid();
        updateHeaderAndEmptyState();

        new Thread(() -> {
            try {
                int userId = IWishManager.getLoggedInUser().getUserId();
                Object res = IWishManager.getClient().sendAndWait(new MarkAllNotificationsAsReadRequest(userId));
                if (res == null) refreshAsync();
                clientSide.helpers.NotificationService.getInstance().requestRefresh();
            } catch (Exception e) {
                e.printStackTrace();
                refreshAsync();
            }
        }, "mark-all-read").start();
    }

    @FXML
    private void onClearAll() {
        if (items.isEmpty()) return;
        
        // Show confirmation dialog
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        
        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(20);
        container.getStyleClass().add("alert-box");
        container.setPrefWidth(380);
        
        // Icon
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.getStyleClass().addAll("alert-icon-container", "alert-icon-warning");
        
        org.kordamp.ikonli.javafx.FontIcon warningIcon = new org.kordamp.ikonli.javafx.FontIcon("fas-exclamation-triangle");
        warningIcon.setIconSize(28);
        warningIcon.setIconColor(javafx.scene.paint.Color.web("#f59e0b"));
        iconContainer.getChildren().add(warningIcon);
        
        javafx.scene.control.Label title = new javafx.scene.control.Label("Clear All Notifications?");
        title.getStyleClass().add("alert-title");
        
        javafx.scene.control.Label message = new javafx.scene.control.Label("This will permanently delete all " + items.size() + " notifications.\nThis action cannot be undone.");
        message.getStyleClass().add("alert-message");
        message.setWrapText(true);
        
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(12);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        
        javafx.scene.control.Button cancelBtn = new javafx.scene.control.Button("Cancel");
        cancelBtn.getStyleClass().add("button-outline");
        cancelBtn.setOnAction(e -> dialogStage.close());
        
        javafx.scene.control.Button clearBtn = new javafx.scene.control.Button("Clear All");
        clearBtn.getStyleClass().add("button-danger");
        clearBtn.setOnAction(e -> {
            dialogStage.close();
            performClearAll();
        });
        
        buttons.getChildren().addAll(cancelBtn, clearBtn);
        container.getChildren().addAll(iconContainer, title, message, buttons);
        
        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);
        
        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        // Ensure stylesheet is loaded
        scene.getStylesheets().add(getClass().getResource("/views/dashboard/dashboard.css").toExternalForm());
        dialogStage.setScene(scene);
        
        // Center over owner window
        javafx.stage.Stage ownerStage = (javafx.stage.Stage) javafx.stage.Stage.getWindows()
            .stream()
            .filter(javafx.stage.Window::isShowing)
            .findFirst()
            .orElse(null);
        if (ownerStage != null) {
            dialogStage.initOwner(ownerStage);
            dialogStage.setOnShown(event -> {
                double ownerX = ownerStage.getX();
                double ownerY = ownerStage.getY();
                double ownerW = ownerStage.getWidth();
                double ownerH = ownerStage.getHeight();
                double dialogW = dialogStage.getWidth();
                double dialogH = dialogStage.getHeight();
                dialogStage.setX(ownerX + (ownerW / 2) - (dialogW / 2));
                dialogStage.setY(ownerY + (ownerH / 2) - (dialogH / 2));
                
                // Play entrance animation
                javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(
                    javafx.util.Duration.millis(200), container);
                scaleIn.setFromX(0.8);
                scaleIn.setFromY(0.8);
                scaleIn.setToX(1.0);
                scaleIn.setToY(1.0);
                
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(200), container);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                
                new javafx.animation.ParallelTransition(scaleIn, fadeIn).play();
            });
        }
        dialogStage.showAndWait();
    }

    private void performClearAll() {
        // Clear from database (soft delete)
        new Thread(() -> {
            try {
                int userId = IWishManager.getLoggedInUser().getUserId();
                IWishManager.getClient().sendAndWait(new dtos.requestDtos.notificationHandler.ClearAllNotificationsRequest(userId));
                clientSide.helpers.NotificationService.getInstance().requestRefresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "clear-all-notifications").start();
        
        items.clear();
        refreshGrid();
        updateHeaderAndEmptyState();
    }

    private void dismissNotification(AppNotification notification) {
        // Clear from database (soft delete)
        new Thread(() -> {
            try {
                IWishManager.getClient().sendAndWait(new dtos.requestDtos.notificationHandler.ClearNotificationRequest(notification.getId()));
                clientSide.helpers.NotificationService.getInstance().dismissNotificationByDbId(notification.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "clear-notification").start();
        
        items.remove(notification);
        refreshGrid();
        updateHeaderAndEmptyState();
    }

    private void markOneRead(AppNotification n) {
        if (n == null || n.isRead()) return;

        n.setRead(true);
        refreshGrid();
        updateHeaderAndEmptyState();

        new Thread(() -> {
            try {
                Object res = IWishManager.getClient().sendAndWait(new MarkNotificationAsReadRequest(n.getId()));
                if (res == null) refreshAsync();
                clientSide.helpers.NotificationService.getInstance().requestRefresh();
            } catch (Exception e) {
                e.printStackTrace();
                refreshAsync();
            }
        }, "mark-one-read").start();
    }

    private void refreshAsync() {
        if (loading) return;
        loading = true;

        new Thread(() -> {
            try {
                int userId = IWishManager.getLoggedInUser().getUserId();
                Object response = IWishManager.getClient().sendAndWait(new GetNotificationsRequest(userId));

                List<AppNotification> mapped = mapToAppNotifications(response);

                Platform.runLater(() -> {
                    items.clear();
                    items.addAll(mapped);
                    refreshGrid();
                    updateHeaderAndEmptyState();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(this::updateHeaderAndEmptyState);
            } finally {
                loading = false;
            }
        }, "refresh-notifications").start();
    }

    private void refreshGrid() {
        notificationsGrid.getChildren().clear();
        
        for (AppNotification item : items) {
            VBox card = createNotificationCard(item);
            notificationsGrid.getChildren().add(card);
        }
    }

    private VBox createNotificationCard(AppNotification notification) {
        VBox card = new VBox(8);
        card.setPrefWidth(300);
        card.setMinWidth(280);
        card.setMaxWidth(320);
        card.setPadding(new Insets(16, 20, 16, 16));
        card.setAlignment(Pos.TOP_LEFT);
        
        // Card styling with left colored border
        String borderColor = notification.getType().getColor();
        card.getStyleClass().add("notif-card-container");
        card.setStyle("-fx-border-color: " + borderColor + " transparent transparent transparent; " +
                      "-fx-border-width: 0 0 0 4; -fx-border-radius: 12; " +
                      "-fx-border-insets: -1; -fx-background-insets: 0;");
        
        // Alternative: Use a wrapper with left border
        HBox wrapper = new HBox(0);
        wrapper.setAlignment(Pos.TOP_LEFT);
        
        // Left border strip
        Region leftBorder = new Region();
        leftBorder.setMinWidth(4);
        leftBorder.setMaxWidth(4);
        leftBorder.setStyle("-fx-background-color: " + borderColor + ";");
        leftBorder.getStyleClass().add("notif-card-left-border");
        
        // Content area
        VBox content = new VBox(8);
        content.setPadding(new Insets(16));
        HBox.setHgrow(content, Priority.ALWAYS);
        
        // Header with icon and title
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        
        // Icon
        FontIcon icon = new FontIcon(notification.getType().getIcon());
        icon.setIconSize(16);
        icon.setIconColor(Color.web(borderColor));
        
        // Title
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.getStyleClass().add("notif-card-title");
        titleLabel.setWrapText(true);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        header.getChildren().addAll(icon, titleLabel);
        
        // Unread indicator (blue dot)
        if (!notification.isRead()) {
            Region dot = new Region();
            dot.setMinSize(8, 8);
            dot.setMaxSize(8, 8);
            dot.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 50%;");
            header.getChildren().add(dot);
        }
        
        // Dismiss (X) button
        Button dismissBtn = new Button();
        FontIcon xIcon = new FontIcon("fas-times");
        xIcon.setIconSize(12);
        xIcon.setIconColor(Color.web("#94a3b8"));
        dismissBtn.setGraphic(xIcon);
        dismissBtn.setStyle("-fx-background-color: transparent; -fx-padding: 4; -fx-cursor: hand;");
        dismissBtn.setOnMouseEntered(e -> xIcon.setIconColor(Color.web("#ef4444")));
        dismissBtn.setOnMouseExited(e -> xIcon.setIconColor(Color.web("#94a3b8")));
        dismissBtn.setOnAction(e -> dismissNotification(notification));
        
        // Header row with dismiss button
        HBox headerRow = new HBox(8);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        headerRow.getChildren().addAll(header, dismissBtn);
        
        // Body text
        Label bodyLabel = new Label(notification.getBody());
        bodyLabel.getStyleClass().add("notif-card-body");
        bodyLabel.setWrapText(true);
        bodyLabel.setMaxWidth(260);
        
        // Timestamp - format relative time
        Label timeLabel = new Label(formatRelativeTime(notification.getCreatedAt()));
        timeLabel.getStyleClass().add("notif-card-time");
        
        content.getChildren().addAll(headerRow, bodyLabel, timeLabel);
        
        // Mark read button (only if unread)
        if (!notification.isRead()) {
            Button markReadBtn = new Button("Mark read");
            markReadBtn.getStyleClass().add("notif-mark-read-btn");
            markReadBtn.setOnAction(e -> markOneRead(notification));
            
            HBox btnContainer = new HBox();
            btnContainer.setAlignment(Pos.CENTER_RIGHT);
            btnContainer.getChildren().add(markReadBtn);
            content.getChildren().add(btnContainer);
        }
        
        // Build card with left border
        card.getChildren().clear();
        card.setPadding(new Insets(0));
        card.getStyleClass().add("notif-card-container");
        
        wrapper.getChildren().addAll(leftBorder, content);
        card.getChildren().add(wrapper);
        
        return card;
    }

    private void updateHeaderAndEmptyState() {
        boolean empty = items.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);

        long unread = items.stream().filter(n -> !n.isRead()).count();
        countLabel.setText("Unread: " + unread + " / " + items.size());
    }

    private List<AppNotification> mapToAppNotifications(Object response) {
        List<AppNotification> result = new ArrayList<>();

        if (response == null) return result;

        if (!(response instanceof List)) {
            System.err.println("Unexpected response type: " + response.getClass());
            return result;
        }

        List<?> list = (List<?>) response;

        for (Object obj : list) {
            if (obj instanceof models.Notification) {
                models.Notification n = (models.Notification) obj;

                result.add(new AppNotification(
                    n.getNotificationId(),
                    n.getTitle(),
                    n.getBody(),
                    n.isRead(),
                    n.getCreatedAt()
                ));
            }
        }
        return result;
    }

    private String formatRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "Just now";
        
        Instant then = timestamp.toInstant();
        Instant now = Instant.now();
        Duration duration = Duration.between(then, now);
        
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return "Just now";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " min ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (seconds < 172800) {
            return "Yesterday";
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + " days ago";
        } else {
            // Format as date for older notifications
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM d, yyyy");
            return sdf.format(timestamp);
        }
    }
}
