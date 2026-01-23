package clientSide.controllers;

import clientSide.appManger.IWishManager;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    public void initialize() {
        if (signInButton != null) {
            signInButton.setOnAction(event -> handleLogin());
        }
        if (registerLink != null) {
            registerLink.setOnAction(event -> handleRegisterNavigation());
        }
    }

    private void handleLogin() {
        String email = emailField != null ? emailField.getText() : "";
        String password = passwordField != null ? passwordField.getText() : "";
        
        if (email.isEmpty() || password.isEmpty()) {
             showError("Please enter email and password.");
             return;
        }

        try {
            System.out.println("Login attempt with: " + email);
            
            // Create Login Request
            dtos.requestDtos.userHandler.LoginRequest request = new dtos.requestDtos.userHandler.LoginRequest(email, password);
            
            // Send Request
            clientSide.ClientConnection param = clientSide.ClientApp.getClientConnection();
            if(param == null){
                showError("Server Connection Failed");
                return;
            }
            
            Object response = param.sendAndWait(request);

            if (response instanceof models.User) {
                // Successful Login
                models.User user = (models.User) response;
                System.out.println("Login Successful: " + user.getUserName());
                
                // Store in Session
                clientSide.ClientSession.getInstance().login(user);
                
                // Navigate to Dashboard
                IWishManager.switchScene("dashboard", "I-Wish Dashboard");
            } else {
                // Login Failed
                showError("Invalid Credentials");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Login Error: " + e.getMessage());
        }
    }

    private void handleRegisterNavigation() {
        try {
            IWishManager.switchScene("register", "I-Wish Register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
