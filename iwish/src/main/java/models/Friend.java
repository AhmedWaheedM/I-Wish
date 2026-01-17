package models;
import java.io.Serializable;
public class Friend implements Serializable {

    private User user1;
    private User user2;
    private String status;

    public Friend() {
    }

    public Friend(User user1, User user2, String status) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
    }

    // Getters & Setters
    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
