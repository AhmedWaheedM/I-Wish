package ClientSide.iWishManger;

import ClientSide.project.App;
import dbLayer.ContributionHandler;
import dbLayer.FriendsHandler;
import dbLayer.ItemHandler;
import dbLayer.UsersHandler;
import dbLayer.WishListHandler;
import dbLayer.WishListItemHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IWishManager {

    private UsersHandler usersHandler;
    private FriendsHandler friendsHandler;
    private ItemHandler itemHandler;
    private WishListHandler wishListHandler;
    private WishListItemHandler wishListItemHandler;
    private ContributionHandler contributionHandler;

    private Stage stage;

    public void start(Stage stage) {
        this.stage = stage;

        // initHandlers();
        showLoginScene();
    }

    private void initHandlers() {
        usersHandler = new UsersHandler();
        friendsHandler = new FriendsHandler();
        itemHandler = new ItemHandler();
        wishListHandler = new WishListHandler(friendsHandler);
        wishListItemHandler = new WishListItemHandler(itemHandler, wishListHandler);
        contributionHandler = new ContributionHandler(wishListHandler, usersHandler);
    }

    private void showLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(
                App.class.getResource("dashboard.fxml")
            );

            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 480);

            stage.setScene(scene);
            stage.setTitle("iWish Manager - Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===== Scene Switching ===== */

    public void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                App.class.getResource(fxml + ".fxml")
            );
            Parent root = loader.load();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
