package serverSide.apis;

import dtos.requestDtos.Item.AddItemRequest;
import dtos.requestDtos.Item.DeleteItemRequest;
import dtos.requestDtos.Item.GetAllItemsRequest;
import dtos.requestDtos.Item.GetItemByIdRequest;
import dtos.requestDtos.Item.GetItemPriceRequest;
import serverSide.dbLayer.ItemHandler;

public class ItemApis {

    private final ItemHandler itemHandler;

    public ItemApis(ItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public Object addItem(AddItemRequest r) {
        itemHandler.addItem(r.getName(), r.getPrice());
        return true;
    }

    public Object deleteItem(DeleteItemRequest r) {
        itemHandler.deleteItem(r.getItemId());
        return true;
    }

    public Object getItemPrice(GetItemPriceRequest r) {
        return itemHandler.getItemPrice(r.getItemId());
    }

    public Object getItemById(GetItemByIdRequest r) {
        return itemHandler.getItemById(r.getItemId());
    }

    public Object getAllItems(GetAllItemsRequest r) {
        return itemHandler.getAllItems();
    }
}
