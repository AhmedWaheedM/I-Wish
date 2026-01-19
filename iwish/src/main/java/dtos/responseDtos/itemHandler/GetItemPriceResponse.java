package dtos.responseDtos.itemHandler;

import dtos.Response;

public class GetItemPriceResponse implements Response {
    private double price;

    public GetItemPriceResponse(double price) {
        this.price = price;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
