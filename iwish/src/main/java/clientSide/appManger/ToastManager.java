package clientSide.appManger;


import clientSide.controllers.ToastController;
import dtos.Notification;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ToastManager {

    private static VBox host;

    public static void init(VBox toastHost) {
        host = toastHost;
    }

    public static void show(Notification n) {
        if (host == null) return; 

        try {
            FXMLLoader loader = new FXMLLoader(ToastManager.class.getResource("/views/components/toast.fxml"));
            Parent toastRoot = loader.load();
            ToastController controller = loader.getController();
            controller.setContent(n.getTitle(), n.getBody());

            toastRoot.setOpacity(0);
            host.getChildren().add(0, toastRoot); 

            FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toastRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            PauseTransition stay = new PauseTransition(Duration.seconds(5));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), toastRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
            seq.setOnFinished(e -> host.getChildren().remove(toastRoot));
            seq.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
