package kz.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DataSource {

    private static DataSource instance;
    private final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    private DataSource() {
        try {
            try (Statement stmt = getConnection().createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS item (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255), " +
                        "price DECIMAL(14,2))");
                stmt.execute("TRUNCATE TABLE item RESTART IDENTITY;");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DataSource", e);
        }
    }


    public Connection getConnection() {
        Connection connection = connectionThreadLocal.get();
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:h2:~/test;AUTO_SERVER=TRUE", "sa", "");
                connectionThreadLocal.set(connection);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get connection", e);
            }
        }
        return connection;
    }

    public void closeConnection() {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close connection", e);
            } finally {
                connectionThreadLocal.remove();
            }
        }
    }

}
