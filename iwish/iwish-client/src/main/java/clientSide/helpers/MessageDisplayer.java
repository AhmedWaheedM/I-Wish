package clientSide.helpers;

import dtos.NotificationDto;

public class MessageDisplayer {

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
        container.setPadding(new javafx.geometry.Insets(32));
        container.setStyle("-fx-background-color: white; -fx-border-color: #d3cbcbff; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
        container.setPrefWidth(380);
        
        // Icon container with red background
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.setStyle("-fx-background-color: #fef2f2; -fx-background-radius: 50%;");
        
        org.kordamp.ikonli.javafx.FontIcon errorIcon = new org.kordamp.ikonli.javafx.FontIcon("fas-times-circle");
        errorIcon.setIconSize(32);
        errorIcon.setIconColor(javafx.scene.paint.Color.web("#c93939ff"));
        iconContainer.getChildren().add(errorIcon);
        
        // Title
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        
        // Message
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(javafx.geometry.Pos.CENTER);
        
        // OK Button
        javafx.scene.control.Button okBtn = new javafx.scene.control.Button("OK");
        okBtn.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 8; -fx-padding: 10 32; " +
                      "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        okBtn.setOnAction(e -> dialogStage.close());
        okBtn.setOnMouseEntered(e -> okBtn.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 8; -fx-padding: 10 32; " +
                      "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"));
        okBtn.setOnMouseExited(e -> okBtn.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 8; -fx-padding: 10 32; " +
                      "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"));
        
        container.getChildren().addAll(iconContainer, titleLabel, messageLabel, okBtn);
        
        // Set initial state for animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);
        
        javafx.scene.Scene scene = new javafx.scene.Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
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
        container.setPadding(new javafx.geometry.Insets(32));
        container.setStyle("-fx-background-color: white; -fx-border-color: #d3cbcbff; -fx-background-radius: 14; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
        container.setPrefWidth(400);

        // Icon container
        javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 50%;");

        org.kordamp.ikonli.javafx.FontIcon questionIcon = new org.kordamp.ikonli.javafx.FontIcon("fas-trash");
        questionIcon.setIconSize(32);
        questionIcon.setIconColor(javafx.scene.paint.Color.web("#ef4444"));
        iconContainer.getChildren().add(questionIcon);

        // Title
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        // Message
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Buttons
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.control.Button cancelBtn = new javafx.scene.control.Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #cbd5e1; -fx-border-width: 1; " +
                          "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 10 24; " +
                          "-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialogStage.close());

        javafx.scene.control.Button confirmBtn = new javafx.scene.control.Button("Confirm");
        confirmBtn.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 8; -fx-padding: 10 24; " +
                           "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; " +
                           "-fx-effect: dropshadow(three-pass-box, rgba(239, 68, 68, 0.35), 10, 0, 0, 4);");
        
        confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 8; -fx-padding: 10 24; " +
                           "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; " + 
                           "-fx-effect: dropshadow(three-pass-box, rgba(239, 68, 68, 0.45), 12, 0, 0, 5);"));
        confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 8; -fx-padding: 10 24; " +
                           "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; " +
                           "-fx-effect: dropshadow(three-pass-box, rgba(239, 68, 68, 0.35), 10, 0, 0, 4);"));

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
