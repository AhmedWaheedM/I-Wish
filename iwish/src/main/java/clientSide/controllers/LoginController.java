package clientSide.controllers;

import clientSide.appManger.IWishManager;
import dtos.requestDtos.userHandler.LoginRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void onLoginClicked() {
        statusLabel.setText("Logging in...");
        statusLabel.setStyle("-fx-text-fill: orange;");

        new Thread(() -> {
            try {
                String userName = usernameField.getText().trim();
                String password = passwordField.getText().trim();

                LoginRequest loginRequest = new LoginRequest(userName, password);
                Object response = IWishManager.getClient().sendAndWait(loginRequest);

                javafx.application.Platform.runLater(() -> {
                    if (!(response instanceof User) || response == null) {
                        statusLabel.setText("Invalid username or password ✘");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    User user = (User) response;
                    IWishManager.setLoggedInUser(user);
                    IWishManager.switchScene("dashboard", "iWish - Dashboard");
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Server error. Please try later.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        }).start();
    }


}
