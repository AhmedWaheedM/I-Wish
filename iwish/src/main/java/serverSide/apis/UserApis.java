package serverSide.apis;

import dtos.requestDtos.userHandler.HasEnoughBalanceRequest;
import dtos.requestDtos.userHandler.LoginRequest;
import dtos.requestDtos.userHandler.RegisterationRequest;
import dtos.requestDtos.userHandler.UpdateBalanceRequest;
import models.User;
import serverSide.dbLayer.UsersHandler;

public class UserApis {

    private final UsersHandler usersHandler;

    public UserApis(UsersHandler usersHandler) {
        this.usersHandler = usersHandler;
    }

    public Object login(LoginRequest r) {
        return usersHandler.Login(r.getUserName(), r.getPassword());
    }

    public Object register(User user) {
        return usersHandler.register(user);
    }

    public Object register(RegisterationRequest r) {
        return usersHandler.register(r.getUser());
    }

    public Object hasEnoughBalance(HasEnoughBalanceRequest r) {
        return usersHandler.hasEnoughBalance(r.getUserId(), r.getAmount());
    }

    public Object updateBalance(UpdateBalanceRequest r) {
        usersHandler.updateBalance(r.getUserId(), r.getAmount(), r.getOperation());
        return true;
    }

    public String getUserNameById(int userId) {
        return usersHandler.getUserNameById(userId);
    }
}
