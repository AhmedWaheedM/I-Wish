package clientSide.controllers;

import models.Friend;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class FriendCardController {

    @FXML
    private Region avatar;
    
    @FXML
    private Label nameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label bioLabel;

    public void setData(Friend friend) {
        // TODO: Fix missing field - methods need to be mapped to models.User or calculated
        /*
        if (nameLabel != null) nameLabel.setText(friend.getName());
        if (usernameLabel != null) usernameLabel.setText("@" + friend.getUsername());
        if (bioLabel != null) bioLabel.setText(friend.getBio());
        */
        
        // Basic mapping attempt if User2 is the friend
        if (friend.getUser2() != null) {
            // Note: User model might not have getName/getUsername exposed the same way, need verification.
            // Assuming User has getUsername().
             if (usernameLabel != null) usernameLabel.setText("@" + friend.getUser2().getUserName());
        }
        
        // Avatar color (assuming 'bg-pink-500' maps to something, or we skip)
        // Ideally we would set style, but for now just text is verified.
    }
}
