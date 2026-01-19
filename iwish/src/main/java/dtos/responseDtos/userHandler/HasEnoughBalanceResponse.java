package dtos.responseDtos.userHandler;

import dtos.Response;

public class HasEnoughBalanceResponse implements Response {
    private boolean hasEnough;

    public HasEnoughBalanceResponse(boolean hasEnough) {
        this.hasEnough = hasEnough;
    }

    public boolean isHasEnough() { return hasEnough; }
    public void setHasEnough(boolean hasEnough) { this.hasEnough = hasEnough; }
}
