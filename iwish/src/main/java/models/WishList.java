package models;
import java.util.List;
public class WishList {

    private int wishListId;
    private double currentAmount;
    private double totalAmount;

    private User user;
    private List<WishListItem> wishListItems;

    public WishList() {
    }

    public WishList(int wishListId, double currentAmount, double totalAmount, User user) {
        this.wishListId = wishListId;
        this.currentAmount = currentAmount;
        this.totalAmount = totalAmount;
        this.user = user;
    }

    // Getters & Setters
    public int getWishListId() {
        return wishListId;
    }

    public void setWishListId(int wishListId) {
        this.wishListId = wishListId;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<WishListItem> getWishListItems() {
        return wishListItems;
    }

    public void setWishListItems(List<WishListItem> wishListItems) {
        this.wishListItems = wishListItems;
    }
}
