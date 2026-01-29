package models;
import java.io.Serializable;
public class Item implements Serializable {

    private int itemId;
    private String name;
    private double price;
    private String description;

    public Item() {
    }

    public Item(int itemId, String name, double price, String description) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
