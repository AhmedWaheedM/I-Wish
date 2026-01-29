package clientSide.helpers;

import dtos.NotificationDto;

public class MessageDisplayer {

    private static final String STYLESHEET_PATH = "/views/dashboard/dashboard.css";

    public static void showError(String message, String title) {
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
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.getStyleClass().add("alert-box");
        
        // Icon container
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.getStyleClass().add("alert-icon-error");
        
        org.kordamp.ikonli.javafx.FontIcon errorIcon = new org.kordamp.ikonli.javafx.FontIcon("fas-times-circle");
        errorIcon.setIconSize(32);
        errorIcon.setIconColor(javafx.scene.paint.Color.web("#c93939"));
        iconContainer.getChildren().add(errorIcon);
        
        // Title
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.getStyleClass().add("alert-title");
        
        // Message
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("alert-message");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(javafx.geometry.Pos.CENTER);
        
        // OK Button
        javafx.scene.control.Button okBtn = new javafx.scene.control.Button("OK");
        okBtn.getStyleClass().add("alert-button-primary");
        okBtn.setOnAction(e -> dialogStage.close());
        
        container.getChildren().addAll(iconContainer, titleLabel, messageLabel, okBtn);
        
        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);
        
        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(MessageDisplayer.class.getResource(STYLESHEET_PATH).toExternalForm());
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
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.getStyleClass().add("alert-box");

        // Icon container
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.getStyleClass().add("alert-icon-confirm");

        org.kordamp.ikonli.javafx.FontIcon questionIcon = new org.kordamp.ikonli.javafx.FontIcon("fas-trash");
        questionIcon.setIconSize(32);
        questionIcon.setIconColor(javafx.scene.paint.Color.web("#ef4444"));
        iconContainer.getChildren().add(questionIcon);

        // Title
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.getStyleClass().add("alert-title");

        // Message
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("alert-message");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Buttons
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.control.Button cancelBtn = new javafx.scene.control.Button("Cancel");
        cancelBtn.getStyleClass().add("alert-button-cancel");
        cancelBtn.setOnAction(e -> dialogStage.close());

        javafx.scene.control.Button confirmBtn = new javafx.scene.control.Button("Confirm");
        confirmBtn.getStyleClass().add("alert-button-danger");

        confirmBtn.setOnAction(e -> {
            onConfirm.run();
            dialogStage.close();
        });

        buttonBox.getChildren().addAll(cancelBtn, confirmBtn);

        container.getChildren().addAll(iconContainer, titleLabel, messageLabel, buttonBox);

        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);

        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(MessageDisplayer.class.getResource(STYLESHEET_PATH).toExternalForm());
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

    public static void showSuccess(String message, String title) {
        // Add to right sidebar notification list
        NotificationService.getInstance().addNotification(
            title,
            message,
            NotificationService.NotificationType.INFO
        );
    }

    public static void showContributionSuccess(String itemName, double amount) {
        String message = String.format("Contribution of $%.2f to %s successful!", amount, itemName);
        NotificationService.getInstance().addNotification(
            "Contribution Success",
            message,
            NotificationService.NotificationType.CONTRIBUTION
        );
    }

    public static void showFundingMilestone(String itemName, int percentage) {
        NotificationService.getInstance().showFundingMilestoneNotification(itemName, percentage);
    }

    public static void showNotification(NotificationDto notification) {
        NotificationService.getInstance().addNotification(
            notification.getTitle(),
            notification.getBody(),
            NotificationService.NotificationType.INFO
        );
    }
}
