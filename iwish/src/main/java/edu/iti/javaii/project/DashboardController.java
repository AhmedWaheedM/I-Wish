package edu.iti.javaii.project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private GridPane itemsGrid;
    @FXML private VBox activityList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMockItems();
        loadMockActivities();
    }

    private void loadMockItems() {
        // Row 1: My Wishlist Items (Remove)
        itemsGrid.add(createItemCard("Apple Watch Series 9", "$399", 200, 399, "Remove"), 0, 0);
        itemsGrid.add(createItemCard("PlayStation 5", "$499", 374.25, 499, "Remove"), 1, 0);
        itemsGrid.add(createItemCard("MacBook Pro 14\"", "$1999", 600, 1999, "Remove"), 2, 0);

        // Row 2: Friends Items (Contribute)
        itemsGrid.add(createItemCard("Sony WH-1000XM5 Headphones", "$399", 279.3, 399, "Contribute"), 0, 1);
        itemsGrid.add(createItemCard("Canon EOS R6 Camera", "$2499", 1249.5, 2499, "Contribute"), 1, 1);
        itemsGrid.add(createItemCard("Gaming Keyboard RGB", "$159", 95.4, 159, "Contribute"), 2, 1);
    }

    private VBox createItemCard(String name, String priceString, double collected, double total, String action) {
        VBox card = new VBox();
        card.getStyleClass().add("item-card");
        card.setPrefWidth(280);
        card.setMinWidth(280);
        
        // Image Placeholder
        VBox imageContainer = new VBox();
        imageContainer.getStyleClass().add("card-image-container");
        Label imgLabel = new Label("Image: " + name); 
        imgLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12px;");
        imageContainer.getChildren().add(imgLabel);

        // Content
        VBox content = new VBox();
        content.getStyleClass().add("card-content");

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("item-name");
        
        // Price Details Row
        HBox priceRow = new HBox();
        priceRow.setAlignment(Pos.BOTTOM_LEFT);
        Label priceLabel = new Label(priceString);
        priceLabel.getStyleClass().add("item-price");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label collectedLabel = new Label("$" + collected + " collected");
        collectedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #718096;");
        
        priceRow.getChildren().addAll(priceLabel, spacer, collectedLabel);

        // Progress Bar
        double progress = collected / total;
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("progress-bar");

        // Progress Text
        HBox progressInfo = new HBox();
        Label funded = new Label((int)(progress * 100) + "% funded");
        funded.setStyle("-fx-font-size: 11px; -fx-text-fill: #718096; -fx-font-weight: bold;");
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Label remaining = new Label("$" + String.format("%.2f", total - collected) + " remaining");
        remaining.setStyle("-fx-font-size: 11px; -fx-text-fill: #718096;");
        
        progressInfo.getChildren().addAll(funded, spacer2, remaining);

        // Action Button
        Button actionBtn = new Button(action);
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        if ("Remove".equals(action)) {
            actionBtn.getStyleClass().add("remove-button");
        } else {
            actionBtn.getStyleClass().add("contribute-button");
        }

        content.getChildren().addAll(nameLabel, priceRow, progressBar, progressInfo, actionBtn);

        card.getChildren().addAll(imageContainer, content);
        
        return card;
    }

    private void loadMockActivities() {
        activityList.getChildren().clear();
        activityList.getChildren().add(createActivityItem("Sara contributed $50 to your PlayStation 5", "2 hours ago", "green"));
        activityList.getChildren().add(createActivityItem("Mike contributed $25 to your Apple Watch", "4 hours ago", "green"));
        activityList.getChildren().add(createActivityItem("Lisa sent you a friend request", "6 hours ago", "purple"));
        activityList.getChildren().add(createActivityItem("Your MacBook Pro reached 30% funding!", "1 day ago", "blue"));
        activityList.getChildren().add(createActivityItem("You contributed $30 to Sara's Headphones", "1 day ago", "green"));
        activityList.getChildren().add(createActivityItem("New wishlist feature: Add multiple images to items", "2 days ago", "orange"));
        activityList.getChildren().add(createActivityItem("Ahmed contributed $100 to your PlayStation 5", "2 days ago", "green"));
        activityList.getChildren().add(createActivityItem("You are now friends with David", "3 days ago", "purple"));
    }

    private VBox createActivityItem(String text, String time, String color) {
        VBox item = new VBox();
        item.getStyleClass().addAll("activity-item", color);
        item.setSpacing(5);

        Label msg = new Label(text);
        msg.getStyleClass().add("activity-text");
        msg.setWrapText(true);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("activity-time");

        item.getChildren().addAll(msg, timeLabel);
        return item;
    }
}
