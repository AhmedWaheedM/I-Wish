package dtos.requestDtos.Item;


import dtos.Request;

public class DeleteItemRequest implements Request {
    private final int itemId;
    public DeleteItemRequest(int itemId) { this.itemId = itemId; }
    public int getItemId() { return itemId; }
}
