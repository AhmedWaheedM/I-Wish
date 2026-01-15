package models;
public class Contribution {

    private User user;
    private WishListItem wishListItem;
    private double amount;

    public Contribution() {
    }

    public Contribution(User user, WishListItem wishListItem, double amount) {
        this.user = user;
        this.wishListItem = wishListItem;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WishListItem getWishListItem() {
        return wishListItem;
    }

    public void setWishListItem(WishListItem wishListItem) {
        this.wishListItem = wishListItem;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
