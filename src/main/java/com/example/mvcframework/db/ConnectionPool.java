package com.example.mvcframework.db;

import com.example.mvcframework.spring.annotation.Component;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConnectionPool {
    private Map<Long, Connection> connections;
    private String host;
    private int port;
    private String dbName;
    private String username;
    private String password;

    public ConnectionPool() {
        this.host = App.DB_HOST;
        this.port = App.DB_PORT;
        this.username = App.DB_ID;
        this.password = App.DB_PASSWORD;
        this.dbName = App.DB_NAME;

        connections = new HashMap<>();
    }


    public Connection getConnection() {
        long currentThreadId = Thread.currentThread().getId();

        if (!connections.containsKey(currentThreadId)) {
            createConnection(currentThreadId);
        }

        return connections.get(currentThreadId);
    }

    private void createConnection(long currentThreadId) {
        loadDriver();

        Connection connection = null;

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeBehavior=convertToNull";
        try {
            connection = DriverManager.getConnection(url, username, password);
            connections.put(currentThreadId, connection);
        } catch (SQLException e) {
            closeConnection();
            throw new MyMapException(e);
        }
    }

    private void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new MyMapException(e);
        }
    }

    public void closeConnection() {
        long currentThreadId = Thread.currentThread().getId();

        if (!connections.containsKey(currentThreadId)) {
            return;
        }

        Connection connection = connections.get(currentThreadId);

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new MyMapException(e);
        }

        connections.remove(currentThreadId);
    }
}