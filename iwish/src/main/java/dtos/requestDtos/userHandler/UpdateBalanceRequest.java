package dtos.requestDtos.userHandler;

public class UpdateBalanceRequest implements dtos.Request {
    private int userId;
    private double amount;
    private char operation;
    public UpdateBalanceRequest(int userId, double amount, char operation) {
        this.userId = userId;
        this.amount = amount;
        this.operation = operation;
    }
    public int getUserId() {
        return userId;
    }
    public double getAmount() {
        return amount;
    }
    public char getOperation() {
        return operation;
    }
}
