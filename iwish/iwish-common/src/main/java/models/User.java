package models;

import java.util.List;
import java.io.Serializable;
public class User implements Serializable {

    private int userId;
    private String userName;
    private String password;
    private double balance;

    private List<WishList> wishLists;

    public User() {
    }

    public User(int userId, String userName, String password, double balance) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.balance = balance;
    }

    // Getters & Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<WishList> getWishLists() {
        return wishLists;
    }

    public void setWishLists(List<WishList> wishLists) {
        this.wishLists = wishLists;
    }
}
