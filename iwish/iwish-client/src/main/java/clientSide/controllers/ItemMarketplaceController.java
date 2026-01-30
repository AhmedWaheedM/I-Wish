package clientSide.controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.Item;

public class ItemMarketplaceController {

    @FXML
    private GridPane marketplaceGrid;

    private int userWishListId = -1;

    @FXML
    public void initialize() {
        System.out.println("ItemMarketplaceController initialized.");
        fetchData();
    }

    private void fetchData() {
        models.User user = clientSide.appManger.IWishManager.getLoggedInUser();
        if (user == null) return;

        try {
            clientSide.ClientConnection conn = clientSide.ClientApp.getClientConnection();
            if (conn == null) return;

            dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest wlReq =
                    new dtos.requestDtos.wishListHandler.GetWishListByUserIdRequest(user.getUserId());

            Object wlResp = conn.sendAndWait(wlReq);
            if (wlResp instanceof models.WishList) {
                this.userWishListId = ((models.WishList) wlResp).getWishListId();
                System.out.println("Marketplace targeting Wishlist ID: " + userWishListId);
            } else {
                System.out.println("Could not fetch wishlist for user.");
                return;
            }

            dtos.requestDtos.Item.GetAllItemsRequest itemReq = new dtos.requestDtos.Item.GetAllItemsRequest();
            Object itemResp = conn.sendAndWait(itemReq);

            if (itemResp instanceof List) {
                @SuppressWarnings("unchecked")
                List<Item> items = (List<Item>) itemResp;

                System.out.println("Marketplace fetched " + items.size() + " items.");
                populateGrid(items);
            } else {
                System.out.println("Unexpected response type for items: " +
                        (itemResp == null ? "null" : itemResp.getClass().getName()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateGrid(List<Item> items) {
        if (marketplaceGrid == null) return;

        marketplaceGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : items) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/market_item_card.fxml"));
                VBox card = loader.load();

                MarketItemCardController controller = loader.getController();

                controller.setData(item, userWishListId);

                marketplaceGrid.add(card, col, row);

                col++;
                if (col == 3) {
                    col = 0;
                    row++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
