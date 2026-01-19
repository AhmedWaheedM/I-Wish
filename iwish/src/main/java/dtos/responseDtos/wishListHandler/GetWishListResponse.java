package dtos.responseDtos.wishListHandler;

import dtos.Response;
import models.WishList;
import java.util.List;

public class GetWishListResponse implements Response {
    private boolean success;
    private List<WishList> wishLists;
    private String errorMessage;

    public GetWishListResponse(boolean success, List<WishList> wishLists, String errorMessage) {
        this.success = success;
        this.wishLists = wishLists;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public List<WishList> getWishLists() { return wishLists; }
    public void setWishLists(List<WishList> wishLists) { this.wishLists = wishLists; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
