package clientSide.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import clientSide.appManger.IWishManager;
import dtos.requestDtos.notificationHandler.GetNotificationsRequest;
import dtos.requestDtos.notificationHandler.MarkAllNotificationsAsReadRequest;
import dtos.requestDtos.notificationHandler.MarkNotificationAsReadRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class NotificationsController {

    // UI model (you can replace with your real model later)
    public static class AppNotification {
        private final int id;
        private final String title;
        private final String body;
        private boolean read;

        public AppNotification(int id, String title, String body, boolean read) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.read = read;
        }
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getBody() { return body; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }

    @FXML private ListView<AppNotification> notificationsList;
    @FXML private Label emptyLabel;
    @FXML private Label countLabel;
    @FXML private Label timeLabel;

    private final ObservableList<AppNotification> items = FXCollections.observableArrayList();
    private volatile boolean loading = false;

    @FXML
    private void initialize() {
        notificationsList.setItems(items);

        notificationsList.setCellFactory(lv -> new ListCell<>() {
            private Parent root;
            private NotificationCellController controller;

            @Override
            protected void updateItem(AppNotification item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (root == null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/notification_cell.fxml"));
                        root = loader.load();
                        controller = loader.getController();
                    } catch (IOException e) {
                        e.printStackTrace();
                        setText(item.getTitle());
                        setGraphic(null);
                        return;
                    }
                }

                controller.setData(item, NotificationsController.this::markOneRead);
                setText(null);
                setGraphic(root);
            }
        });

        refreshAsync(); // load first time
        updateHeaderAndEmptyState();
    }

    @FXML
    private void onRefresh() {
        refreshAsync();
    }

    @FXML
    private void onMarkAllRead() {
        if (items.isEmpty()) return;

        // optimistic UI
        items.forEach(n -> n.setRead(true));
        notificationsList.refresh();
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

        // optimistic UI
        n.setRead(true);
        notificationsList.refresh();
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
                    items.setAll(mapped);
                    notificationsList.refresh();
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
