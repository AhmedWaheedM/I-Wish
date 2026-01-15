package dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemHandler extends DBHandler {
    public ItemHandler() {
        super("Item");
    }

    public void addItem(String itemName, double itemPrice) {
        String query = "INSERT INTO " + tableName + " (name, price) VALUES (?, ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, itemName);
            pstmt.setDouble(2, itemPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();    
        }
    }

    public void deleteItem(int itemId) {
        String query = "DELETE FROM " + tableName + " WHERE item_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    
    public double getItemPrice(int itemId) {
        String query = "SELECT price FROM " + tableName + " WHERE item_id = ?";
        double price = 0.0;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, itemId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                price = resultSet.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return price;
    }

    public double getTotalPriceForItems(List<Integer> itemIds) {
        double totalPrice = 0.0;
        String query = "SELECT price FROM " + tableName + " WHERE item_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            for (int itemId : itemIds) {
                pstmt.setInt(1, itemId);
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    totalPrice += resultSet.getDouble("price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return totalPrice;
    }
    
}