package com.kahzerx.kahzerxmod.extensions.shopExtension.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankDatabase {
    private Connection connection = null;
    private BankQuery query = null;

    public void initializeConnection(String dirName) {
        try {
            Class.forName("org.sqlite.JDBC");
            String DATABASE_DIR = "KData";
            String DATABASE_NAME = "KBank.db";

            @SuppressWarnings("unused")
            boolean createDir = new File(String.format("%s/%s", dirName, DATABASE_DIR)).mkdirs();
            connection = DriverManager.getConnection(String.format(
                    "jdbc:sqlite:%s/%s/%s",
                    dirName,
                    DATABASE_DIR,
                    DATABASE_NAME
            ));
            query = new BankQuery(connection);

            connection.createStatement().executeUpdate("PRAGMA foreign_keys=ON");
            connection.createStatement().executeUpdate("PRAGMA secure_delete=ON");
            connection.createStatement().executeUpdate("PRAGMA auto_vacuum=1");
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

    public BankQuery getQuery() {
        return query;
    }
}
