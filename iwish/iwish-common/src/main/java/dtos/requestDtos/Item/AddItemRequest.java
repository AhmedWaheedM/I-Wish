package dtos.requestDtos.Item;


import dtos.Request;

public class AddItemRequest implements Request {
    private final String name;
    private final double price;

    public AddItemRequest(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}
