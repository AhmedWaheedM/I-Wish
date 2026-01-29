package dtos.requestDtos.userHandler;

import dtos.Request;
import models.User;

public class RegisterationRequest implements Request {

    private  User user;

    public RegisterationRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
