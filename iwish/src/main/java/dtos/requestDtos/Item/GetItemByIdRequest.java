package dtos.requestDtos.Item;

import dtos.Request;

public class GetItemByIdRequest implements  Request{

    private final Integer itemId;

    public GetItemByIdRequest(Integer itemId){
        this.itemId = itemId;
    }

    public Integer getItemId(){
        return itemId ;
    }

}
