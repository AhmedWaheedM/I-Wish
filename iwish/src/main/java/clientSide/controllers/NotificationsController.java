package clientSide.controllers;

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

        public AppNotification(int id, String title, String body, boolean read) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.read = read;
            this.type = determineType(title, body);
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
            } catch (Exception e) {
                e.printStackTrace();
                refreshAsync();
            }
        }, "mark-all-read").start();
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
        card.setStyle(String.format(
            "-fx-background-color: white; -fx-background-radius: 12; " +
            "-fx-border-color: %s transparent transparent transparent; " +
            "-fx-border-width: 0 0 0 4; -fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);" +
            "-fx-border-insets: -1; -fx-background-insets: 0;",
            borderColor
        ));
        
        // Alternative: Use a wrapper with left border
        HBox wrapper = new HBox(0);
        wrapper.setAlignment(Pos.TOP_LEFT);
        
        // Left border strip
        Region leftBorder = new Region();
        leftBorder.setMinWidth(4);
        leftBorder.setMaxWidth(4);
        leftBorder.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 12 0 0 12;", borderColor));
        
        // Content area
        VBox content = new VBox(8);
        content.setPadding(new Insets(16));
        HBox.setHgrow(content, Priority.ALWAYS);
        
        // Header with icon and title
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Icon
        FontIcon icon = new FontIcon(notification.getType().getIcon());
        icon.setIconSize(16);
        icon.setIconColor(Color.web(borderColor));
        
        // Title
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        titleLabel.setWrapText(true);
        
        header.getChildren().addAll(icon, titleLabel);
        
        // Unread indicator (blue dot)
        if (!notification.isRead()) {
            Region dot = new Region();
            dot.setMinSize(8, 8);
            dot.setMaxSize(8, 8);
            dot.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 50%;");
            header.getChildren().add(dot);
        }
        
        // Body text
        Label bodyLabel = new Label(notification.getBody());
        bodyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        bodyLabel.setWrapText(true);
        bodyLabel.setMaxWidth(260);
        
        // Timestamp placeholder
        Label timeLabel = new Label("Just now");
        timeLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        
        content.getChildren().addAll(header, bodyLabel, timeLabel);
        
        // Mark read button (only if unread)
        if (!notification.isRead()) {
            Button markReadBtn = new Button("Mark read");
            markReadBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; " +
                                 "-fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
            markReadBtn.setOnAction(e -> markOneRead(notification));
            
            HBox btnContainer = new HBox();
            btnContainer.setAlignment(Pos.CENTER_RIGHT);
            btnContainer.getChildren().add(markReadBtn);
            content.getChildren().add(btnContainer);
        }
        
        // Build card with left border
        card.getChildren().clear();
        card.setPadding(new Insets(0));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
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
                    n.isRead()
                ));
            }
        }
        return result;
    }
}
