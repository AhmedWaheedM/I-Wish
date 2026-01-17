package dtos.requestDtos.userHandler;

public class LoginRequest implements dtos.Request {

    private String Email;
    private String Password;

    public LoginRequest(String Email, String Password) {
        this.Email = Email;
        this.Password = Password;
    }

    public String getEmail() {
        return Email;
    }
    public String getPassword() {
        return Password;
    }
}
