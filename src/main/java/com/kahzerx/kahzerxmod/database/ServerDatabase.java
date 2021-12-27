package com.kahzerx.kahzerxmod.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ServerDatabase {
    private Connection connection = null;
    private ServerQuery query = null;

    public void initializeConnection(String dirName) {
        try {
            Class.forName("org.sqlite.JDBC");
            String DATABASE_DIR = "KData";
            String DATABASE_NAME = "KServer.db";

            @SuppressWarnings("unused")
            boolean createDir = new File(String.format("%s/%s", dirName, DATABASE_DIR)).mkdirs();
            connection = DriverManager.getConnection(String.format(
                    "jdbc:sqlite:%s/%s/%s",
                    dirName,
                    DATABASE_DIR,
                    DATABASE_NAME
            ));
            query = new ServerQuery(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnectionAlive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void createPlayerTable() {
        try {
            String createPlayer = "CREATE TABLE IF NOT EXISTS `player` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`name` VARCHAR(50) DEFAULT NULL);";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createPlayer);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (isConnectionAlive()) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public ServerQuery getQuery() {
        return query;
    }
}
