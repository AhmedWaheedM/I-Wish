package clientSide.appManger;

import clientSide.helpers.NotificationService;
import dtos.NotificationDto;

/**
 * ToastManager now routes all notifications to the right sidebar instead of showing
 * a top bar toast. Maintains the same API for backward compatibility.
 */
public class ToastManager {

    // No longer needed, but kept for backward compatibility
    public static void init(javafx.scene.layout.VBox toastHost) {
        // No-op: notifications now go to the right sidebar
    }

    /**
     * Route notification to the right sidebar instead of showing a toast.
     */
    public static void show(NotificationDto n) {
        if (n == null) return;

        // Determine notification type based on content
        NotificationService.NotificationType type = determineNotificationType(n);

        // Add to the right sidebar
        NotificationService.getInstance().addNotification(
            n.getTitle(),
            n.getBody(),
            type
        );
    }

    /**
     * Determine the notification type based on the notification content.
     */
    private static NotificationService.NotificationType determineNotificationType(NotificationDto n) {
        String title = n.getTitle() != null ? n.getTitle().toLowerCase() : "";
        String body = n.getBody() != null ? n.getBody().toLowerCase() : "";

        // Contribution notifications
        if (title.contains("contribution") || body.contains("contributed")) {
            return NotificationService.NotificationType.CONTRIBUTION;
        }

        // Friend request notifications
        if (title.contains("friend request") || body.contains("friend request")) {
            return NotificationService.NotificationType.FRIEND_REQUEST;
        }

        // Friend accepted notifications
        if (title.contains("accepted") || body.contains("accepted")) {
            return NotificationService.NotificationType.FRIEND_ACCEPTED;
        }

        // Funding/milestone notifications
        if (title.contains("funding") || title.contains("milestone") || 
            body.contains("funded") || body.contains("%")) {
            return NotificationService.NotificationType.FUNDING_MILESTONE;
        }

        // Wishlist notifications
        if (title.contains("wishlist") || body.contains("wishlist")) {
            return NotificationService.NotificationType.WISHLIST_UPDATE;
        }

        // Default to info
        return NotificationService.NotificationType.INFO;
    }
}
