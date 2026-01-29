package dtos.requestDtos.contributionHandler;

import dtos.Request;

public class AddContributionRequest implements Request {
    private final int userId;
    private final int wishListId;
    private final int wishListItemId;
    private final double amount;

    public AddContributionRequest(int userId, int wishListId, int wishListItemId, double amount) {
        this.userId = userId;
        this.wishListId = wishListId;
        this.wishListItemId = wishListItemId;
        this.amount = amount;
    }

    public int getUserId() { return userId; }
    public int getWishListId() { return wishListId; }
    public int getWishListItemId() { return wishListItemId; }
    public double getAmount() { return amount; }
}
