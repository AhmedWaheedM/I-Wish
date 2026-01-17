package dtos.requestDtos.userHandler;

public class HasEnoughBalanceRequest implements dtos.Request {
    int userId;
    double amount;

    public HasEnoughBalanceRequest(int userId, double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public int getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }
}
