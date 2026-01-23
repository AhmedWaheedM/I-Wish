package serverSide.dbLayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import models.User;

public class UsersHandler extends DBHandler {
    public UsersHandler() {
        super("User");
    }
    private boolean userExists(String username) {
        String query = "SELECT COUNT(*) AS count FROM " + tableName + " WHERE username = ?";
        boolean exists = false;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                exists = resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return exists;
    }
    public boolean  register(User user) {

        if(userExists(user.getUserName())){
            return false;
        }
        String query = "INSERT INTO " + tableName + " (username, password, balance) VALUES (?, ?, ?)";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setDouble(3, user.getBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return true;
    }
    public User Login(String username, String password) {
        String query = "SELECT * FROM " + tableName + " WHERE username = ? AND password = ?";
        User user = null;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setUserId(resultSet.getInt("user_id"));
                user.setUserName(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setBalance(resultSet.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return user;
    }
    public boolean hasEnoughBalance(int userId, double amount) {
        String query = "SELECT balance FROM " + tableName + " WHERE user_id = ?";
        boolean hasEnough = false;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                hasEnough = balance >= amount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return hasEnough;
    }
    public void updateBalance(int userId, double amount , char operation) {
        String query = "UPDATE " + tableName + " SET balance = balance " + operation + " ? WHERE user_id = ?";
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public User getUserById(int userId) {
        String query = "SELECT * FROM " + tableName + " WHERE user_id = ?";
        User user = null;
        try {
            connect();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setUserId(resultSet.getInt("user_id"));
                user.setUserName(resultSet.getString("username"));
                user.setBalance(resultSet.getDouble("balance"));
                // password usually not needed for public info
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return user;
    }
}