package models;

import java.io.Serializable;
import java.util.List;
public class WishListItem implements Serializable {

    private int recId;
    private int wishListId;
    private int quantity;

    private Item item;
    private List<Contribution> contributions;

    public WishListItem() {
    }

    public WishListItem(int recId, int wishListId, Item item) {
        this.recId = recId;
        this.wishListId = wishListId;
        this.item = item;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public int getWishListId() {
        return wishListId;
    }

    public void setWishListId(int wishListId) {
        this.wishListId = wishListId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
