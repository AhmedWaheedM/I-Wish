package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Item;

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
            return -1;
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
            return -1;
        } finally {
            close();
        }
        return totalPrice;
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();
        String query = "SELECT item_id, name, price, description FROM " + tableName;

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                Item item = new Item();
                item.setItemId(resultSet.getInt("item_id"));
                item.setName(resultSet.getString("name"));
                item.setPrice(resultSet.getDouble("price"));
                item.setDescription(resultSet.getString("description"));

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }

        return items;
    }

    public Item getItemById(int itemId) {
        Item item = null;
        String query = "SELECT item_id, name, price, description FROM " + tableName + " WHERE item_id = ?";

        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, itemId);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                item = new Item();
                item.setItemId(resultSet.getInt("item_id"));
                item.setName(resultSet.getString("name"));
                item.setPrice(resultSet.getDouble("price"));
                item.setDescription(resultSet.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }

        return item;
}

    
}