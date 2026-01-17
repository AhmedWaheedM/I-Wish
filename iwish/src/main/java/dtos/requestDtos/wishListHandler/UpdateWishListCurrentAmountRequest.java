package dtos.requestDtos.wishListHandler;


import dtos.Request;

public class UpdateWishListCurrentAmountRequest implements Request {
    private final int wishListId;
    private final double amount;
    private final char operation;

    public UpdateWishListCurrentAmountRequest(int wishListId, double amount, char operation) {
        this.wishListId = wishListId;
        this.amount = amount;
        this.operation = operation;
    }

    public int getWishListId() { return wishListId; }
    public double getAmount() { return amount; }
    public char getOperation() { return operation; }
}
