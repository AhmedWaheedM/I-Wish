package clientSide.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import models.Friend;

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

        
        if (friend.getUser2() != null) {

             if (usernameLabel != null) usernameLabel.setText("@" + friend.getUser2().getUserName());
        }

    }
}
