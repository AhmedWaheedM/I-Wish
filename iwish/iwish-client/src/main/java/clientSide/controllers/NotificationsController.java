package clientSide.controllers;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import clientSide.appManger.IWishManager;
import clientSide.helpers.MessageDisplayer;
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
        CONTRIBUTION("#22c55e", "fas-dollar-sign", "notif-left-border-green"),
        FRIEND_REQUEST("#3b82f6", "fas-user-plus", "notif-left-border-blue"),
        FUNDING_MILESTONE("#22c55e", "fas-chart-line", "notif-left-border-green"),
        FRIEND_ACCEPTED("#a855f7", "fas-user-check", "notif-left-border-purple"),
        FRIEND_REMOVED("#ef4444", "fas-user-minus", "notif-left-border-red"),
        INFO("#6b7280", "fas-bell", "notif-left-border-gray");

        private final String color;
        private final String icon;
        private final String cssClass;

        NotificationType(String color, String icon, String cssClass) {
            this.color = color;
            this.icon = icon;
            this.cssClass = cssClass;
        }

        public String getColor() { return color; }
        public String getIcon() { return icon; }
        public String getCssClass() { return cssClass; }
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
        
        String message = "This will permanently delete all " + items.size() + " notifications.\nThis action cannot be undone.";
        MessageDisplayer.showConfirmation("Clear All Notifications?", message, this::performClearAll);
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
        card.setPadding(new Insets(0));
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("notif-card");
        
        // Wrapper with left border
        HBox wrapper = new HBox(0);
        wrapper.setAlignment(Pos.TOP_LEFT);
        
        // Left border strip - use CSS class based on type
        Region leftBorder = new Region();
        leftBorder.getStyleClass().add(notification.getType().getCssClass());
        
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
        icon.setIconColor(Color.web(notification.getType().getColor()));
        
        // Title
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.getStyleClass().add("notif-title");
        titleLabel.setWrapText(true);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        header.getChildren().addAll(icon, titleLabel);
        
        // Unread indicator (blue dot)
        if (!notification.isRead()) {
            Region dot = new Region();
            dot.getStyleClass().add("notif-unread-dot");
            header.getChildren().add(dot);
        }
        
        // Dismiss (X) button
        Button dismissBtn = new Button();
        FontIcon xIcon = new FontIcon("fas-times");
        xIcon.setIconSize(12);
        xIcon.setIconColor(Color.web("#94a3b8"));
        dismissBtn.setGraphic(xIcon);
        dismissBtn.getStyleClass().add("notif-dismiss-btn");
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
        bodyLabel.getStyleClass().add("notif-body");
        bodyLabel.setWrapText(true);
        bodyLabel.setMaxWidth(260);
        
        // Timestamp - format relative time
        Label timeLabel = new Label(formatRelativeTime(notification.getCreatedAt()));
        timeLabel.getStyleClass().add("notif-time");
        
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
