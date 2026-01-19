package serverSide.dbLayer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public abstract class DBHandler {
    protected static String dbUrl ;
    protected static String dbName ;
    protected static String username ;
    protected static String password ;
    protected String tableName;

    protected Connection connection;
    protected Statement statement;
    protected ResultSet resultSet;

    static {
        try (InputStream input =
                    DBHandler.class
                            .getClassLoader()
                            .getResourceAsStream("db.properties")
            ) {

            if (input == null) {
                throw new RuntimeException("db.properties not found in resources");
            }

            Properties props = new Properties();
            props.load(input);

            dbUrl = props.getProperty("db.url");
            dbName = props.getProperty("db.name");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB configuration", e);
        }
    }

    public DBHandler(String tableName) {
        this.tableName = tableName;
    }

    protected void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {

            try {
                connection = DriverManager.getConnection(dbUrl, username, password);
                System.err.println("Connection established ");
            } catch (Exception e) {
                System.err.println("error: " + e.getMessage());
            }
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