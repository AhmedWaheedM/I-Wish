package dtos.requestDtos.Item;


import dtos.Request;

public class GetItemPriceRequest implements Request {
    private final int itemId;
    public GetItemPriceRequest(int itemId) { this.itemId = itemId; }
    public int getItemId() { return itemId; }
}
