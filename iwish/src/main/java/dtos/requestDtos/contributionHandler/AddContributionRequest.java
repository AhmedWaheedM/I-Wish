package dtos.requestDtos.contributionHandler;

import dtos.Request;

public class AddContributionRequest implements Request {
    private final int userId;
    private final int wishListId;
    private final double amount;

    public AddContributionRequest(int userId, int wishListId, double amount) {
        this.userId = userId;
        this.wishListId = wishListId;
        this.amount = amount;
    }

    public int getUserId() { return userId; }
    public int getWishListId() { return wishListId; }
    public double getAmount() { return amount; }
}
