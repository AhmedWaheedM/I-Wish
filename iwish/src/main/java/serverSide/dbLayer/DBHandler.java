package serverSide.dbLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBHandler {
    protected final String dbUrl = "jdbc:mysql://localhost:3306/your_db";
    protected final String dbName = "your_db";
    protected final String username = "root";
    protected final String password = "password";
    protected String tableName;

    protected Connection connection;
    protected Statement statement;
    protected ResultSet resultSet;

    public DBHandler(String tableName) {
        this.tableName = tableName;
    }

    protected void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, username, password);
        }
    }

    protected void close() {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}