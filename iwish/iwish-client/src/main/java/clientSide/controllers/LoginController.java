package clientSide.controllers;

import clientSide.appManger.IWishManager;
import clientSide.helpers.MessageDisplayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        
        // Add Enter Key Handler to fields
        if (emailField != null) {
            emailField.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) handleLogin();
            });
        }
        if (passwordField != null) {
            passwordField.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) handleLogin();
            });
        }
    }

    private void handleLogin() {
        String email = emailField != null ? emailField.getText().trim() : "";
        String password = passwordField != null ? passwordField.getText() : "";
        
        if (email.isEmpty() || password.isEmpty()) {
             MessageDisplayer.showError("Please enter email and password." , "Login error");
             return;
        }

        try {
            System.out.println("Login attempt with: " + email);
            
            // Create Login Request
            dtos.requestDtos.userHandler.LoginRequest request = new dtos.requestDtos.userHandler.LoginRequest(email, password);
            
            // Send Request
            clientSide.ClientConnection param = clientSide.ClientApp.getClientConnection();
            if(param == null){
                MessageDisplayer.showError("Server Connection Failed" , "Login error");
                return;
            }
            
            Object response = param.sendAndWait(request);

            if (response instanceof models.User) {
                // Successful Login
                models.User user = (models.User) response;
                System.out.println("Login Successful: " + user.getUserName());
                
                // Store in Session
                clientSide.appManger.IWishManager.login(user);
                
                // Navigate to Dashboard
                IWishManager.switchScene("dashboard", "I-Wish Dashboard");
            } else {
                // Login Failed
                MessageDisplayer.showError("Invalid Credentials" , "Login Error");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            MessageDisplayer.showError("Login Error: " + e.getMessage() , "Login Error");
        }
    }

    private void handleRegisterNavigation() {
        try {
            IWishManager.switchScene("register", "I-Wish Register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    

    @FXML
    private void onRegisterClicked(ActionEvent event) {
        IWishManager.switchScene("register", "iWish - Register");
    }
}
