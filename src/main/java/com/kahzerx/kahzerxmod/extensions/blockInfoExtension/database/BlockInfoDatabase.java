package com.kahzerx.kahzerxmod.extensions.blockInfoExtension.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BlockInfoDatabase {
    private Connection connection = null;
    private BlockInfoQuery query = null;

    public void initializeConnection(String dirName) {
        try {
            Class.forName("org.sqlite.JDBC");
            String DATABASE_DIR = "KData";
            String DATABASE_NAME = "KBlockInfo.db";

            @SuppressWarnings("unused")
            boolean createDir = new File(String.format("%s/%s", dirName, DATABASE_DIR)).mkdirs();
            connection = DriverManager.getConnection(String.format(
                    "jdbc:sqlite:%s/%s/%s",
                    dirName,
                    DATABASE_DIR,
                    DATABASE_NAME
            ));
            query = new BlockInfoQuery(connection);

            connection.createStatement().executeUpdate("PRAGMA foreign_keys=ON");
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

    public BlockInfoQuery getQuery() {
        return query;
    }
}
