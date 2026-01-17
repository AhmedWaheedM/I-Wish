package dtos.requestDtos.contributionHandler;
import dtos.Request;

public class RemoveContributionRequest implements Request {
    private final int contributionId;
    private final int userId;
    private final int wishListId;

    public RemoveContributionRequest(int contributionId, int userId, int wishListId) {
        this.contributionId = contributionId;
        this.userId = userId;
        this.wishListId = wishListId;
    }

    public int getContributionId() { return contributionId; }
    public int getUserId() { return userId; }
    public int getWishListId() { return wishListId; }
}