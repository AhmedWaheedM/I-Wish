package clientSide.helpers;

import java.io.InputStream;
import java.util.Locale;

import javafx.scene.image.Image;

public final class ItemImageSelector {

    private ItemImageSelector() {}

    public static Image getImageByItemName(String itemName) {
        String normalized = safe(itemName).trim().toLowerCase(Locale.ROOT);

        String resourcePath = null;

        System.err.println("Selecting image for item name: '" + normalized + "'");
        if (normalized.equals("headphones")) {
            resourcePath = "/statics/HeadPhone.png";
        } else if (normalized.equals("smart watch")) {
            resourcePath = "/statics/Watch.png";
        } else if (normalized.equals("backpack")) {
            resourcePath = "/statics/LapBag.png";
        }

        if (resourcePath == null) return null;

        try (InputStream is = ItemImageSelector.class.getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            return new Image(is);
        } catch (Exception e) {
            System.out.println("Failed to load image resource: " + resourcePath + " -> " + e.getMessage());
            return null;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
