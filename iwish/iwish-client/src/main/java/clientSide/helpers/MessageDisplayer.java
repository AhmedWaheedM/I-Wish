package clientSide.helpers;

import models.Notification;

public class MessageDisplayer {

    public static void showError(String message, String title) {
        showDialog(message, title, "alert-icon-error", "fas-times-circle", "#c93939ff");
    }

    public static void showSuccess(String message, String title) {
        // Also show a dialog for success if needed, or just notification + dialog
        // For now, let's keep the original logic of adding to sidebar, but also show a dialog if explicitly called
        // The original code only added to sidebar. If we want a popup, we can use showDialog.
        // But based on method name, it seems used for notifications.
        // Let's check original implementation: it called NotificationService.
        
        NotificationService.getInstance().addNotification(
            title,
            message,
            NotificationService.NotificationType.INFO
        );
    }
    
    // Helper to show a generic styled dialog
    private static void showDialog(String message, String title, String iconClass, String iconLiteral, String iconColorWeb) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle(title);
        
        // Set owner to center over the current window
        javafx.stage.Stage ownerStage = (javafx.stage.Stage) javafx.stage.Stage.getWindows()
            .stream()
            .filter(javafx.stage.Window::isShowing)
            .findFirst()
            .orElse(null);
        if (ownerStage != null) {
            dialogStage.initOwner(ownerStage);
        }
        
        // Main container
        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(20);
        container.getStyleClass().add("alert-box");
        container.setPrefWidth(380);
        
        // Icon container
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.getStyleClass().addAll("alert-icon-container", iconClass);
        
        org.kordamp.ikonli.javafx.FontIcon icon = new org.kordamp.ikonli.javafx.FontIcon(iconLiteral);
        icon.setIconSize(32);
        icon.setIconColor(javafx.scene.paint.Color.web(iconColorWeb));
        iconContainer.getChildren().add(icon);
        
        // Title
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.getStyleClass().add("alert-title");
        
        // Message
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("alert-message");
        messageLabel.setWrapText(true);
        
        // OK Button
        javafx.scene.control.Button okBtn = new javafx.scene.control.Button("OK");
        okBtn.getStyleClass().add("button-primary"); // Reusing existing primary button class
        okBtn.setPrefWidth(120);
        okBtn.setOnAction(e -> dialogStage.close());
        
        container.getChildren().addAll(iconContainer, titleLabel, messageLabel, okBtn);
        
        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);
        
        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        // Load CSS
        scene.getStylesheets().add(MessageDisplayer.class.getResource("/views/dashboard/dashboard.css").toExternalForm());
        
        dialogStage.setScene(scene);
        
        if (ownerStage != null) {
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
                
                javafx.animation.ParallelTransition entrance = new javafx.animation.ParallelTransition(scaleIn, fadeIn);
                entrance.play();
            });
        } else {
            dialogStage.centerOnScreen();
        }        
        dialogStage.showAndWait();
    }

    public static void showContributionSuccess(String itemName, double amount) {
        String message = String.format("Contribution of $%.2f to %s successful!", amount, itemName);
        NotificationService.getInstance().addNotification(
            "Contribution Success",
            message,
            NotificationService.NotificationType.CONTRIBUTION
        );
        // Optional: Show a dialog too if desired, but original only showed notification
    }

    public static void showFundingMilestone(String itemName, int percentage) {
        NotificationService.getInstance().showFundingMilestoneNotification(itemName, percentage);
    }

    public static void showNotification(Notification notification) {
        NotificationService.getInstance().addNotification(
            notification.getTitle(),
            notification.getBody(),
            NotificationService.NotificationType.INFO
        );
    }

    public static void showConfirmation(String title, String message, Runnable onConfirm) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle(title);
        
        // Set owner to center over the current window
        javafx.stage.Stage ownerStage = (javafx.stage.Stage) javafx.stage.Stage.getWindows()
            .stream()
            .filter(javafx.stage.Window::isShowing)
            .findFirst()
            .orElse(null);
        if (ownerStage != null) {
            dialogStage.initOwner(ownerStage);
        }
        
        // Main container
        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(20);
        container.getStyleClass().add("alert-box");
        container.setPrefWidth(380);
        
        // Icon container (Warning/Danger)
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.getStyleClass().addAll("alert-icon-container", "alert-icon-warning");
        
        org.kordamp.ikonli.javafx.FontIcon icon = new org.kordamp.ikonli.javafx.FontIcon("fas-exclamation-triangle");
        icon.setIconSize(32);
        icon.setIconColor(javafx.scene.paint.Color.web("#f59e0b"));
        iconContainer.getChildren().add(icon);
        
        // Title
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.getStyleClass().add("alert-title");
        
        // Message
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("alert-message");
        messageLabel.setWrapText(true);
        
        // Buttons
        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(12);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        
        javafx.scene.control.Button cancelBtn = new javafx.scene.control.Button("Cancel");
        cancelBtn.getStyleClass().add("button-outline");
        cancelBtn.setOnAction(e -> dialogStage.close());
        
        javafx.scene.control.Button confirmBtn = new javafx.scene.control.Button("Confirm");
        confirmBtn.getStyleClass().add("button-danger");
        confirmBtn.setOnAction(e -> {
            dialogStage.close();
            if (onConfirm != null) {
                onConfirm.run();
            }
        });
        
        buttons.getChildren().addAll(cancelBtn, confirmBtn);
        
        container.getChildren().addAll(iconContainer, titleLabel, messageLabel, buttons);
        
        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);
        
        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        // Load CSS
        scene.getStylesheets().add(MessageDisplayer.class.getResource("/views/dashboard/dashboard.css").toExternalForm());
        
        dialogStage.setScene(scene);
        
        if (ownerStage != null) {
            dialogStage.setOnShown(event -> {
                double ownerX = ownerStage.getX();
                double ownerY = ownerStage.getY();
                double ownerW = ownerStage.getWidth();
                double ownerH = ownerStage.getHeight();
                double dialogW = dialogStage.getWidth();
                double dialogH = dialogStage.getHeight();
                dialogStage.setX(ownerX + (ownerW / 2) - (dialogW / 2));
                dialogStage.setY(ownerY + (ownerH / 2) - (dialogH / 2));
                
                // Animation
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
}
