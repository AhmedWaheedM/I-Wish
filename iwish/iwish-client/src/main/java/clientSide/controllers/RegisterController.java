package clientSide.controllers;

import clientSide.appManger.IWishManager;
import clientSide.helpers.MessageDisplayer;
import dtos.requestDtos.userHandler.RegisterationRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button createAccountButton;


    @FXML
    private void handleRegister() {
        String username = usernameField != null ? usernameField.getText().trim() : "";
        String password = passwordField != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : "";

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            MessageDisplayer.showError("Please fill in all fields." , "Registration Error");
            return;
        }

        if (!password.equals(confirmPassword)) {
            MessageDisplayer.showError("Passwords do not match." , "Registration Error");
            return;
        }

        try {
            RegisterationRequest request = new RegisterationRequest(new models.User(0,username, password, 0.0));
            Object response = clientSide.ClientApp.getClientConnection().sendAndWait(request);

            if (!(response instanceof Boolean) || !(Boolean) response) {
                MessageDisplayer.showError("Username already exists. Please choose a different username." , "Registration Error");
                return;
            }

            MessageDisplayer.showSuccess("Registration successful! You can now log in." , "Registration Success");
            IWishManager.switchScene("login", "iWish - Login");


        } catch (Exception e) {
            MessageDisplayer.showError("Registration failed: " + e.getMessage() , "Registration Error");
        }
    }


    @FXML
    private void onLoginClicked(ActionEvent event) {
        IWishManager.switchScene("login", "iWish - Login");
    }
}
