package dtos.requestDtos.userHandler;

public class LoginRequest implements dtos.Request {

    private String UserName;
    private String Password;

    public LoginRequest(String UserName, String Password) {
        this.UserName = UserName;
        this.Password = Password;
    }

    public String getUserName() {
        return UserName;
    }
    public String getPassword() {
        return Password;
    }
}
