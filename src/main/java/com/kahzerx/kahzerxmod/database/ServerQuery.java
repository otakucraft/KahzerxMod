package com.kahzerx.kahzerxmod.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public record ServerQuery(Connection connection) {
    public void insertPlayerUUID(String uuid, String name) {
        try {
            String insertUUID = "INSERT OR IGNORE INTO player (uuid, name) VALUES (?, ?)";
            PreparedStatement insertPlayer = connection.prepareStatement(insertUUID);
            insertPlayer.setString(1, uuid);
            insertPlayer.setString(2, name);
            insertPlayer.executeUpdate();
            insertPlayer.close();
            updateName(uuid, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateName(String uuid, String name) {
        try {
            String updateName = "UPDATE player SET name = ? WHERE uuid = ?";
            PreparedStatement updatePlayerName = connection.prepareStatement(updateName);
            updatePlayerName.setString(1, name);
            updatePlayerName.setString(2, uuid);
            updatePlayerName.executeUpdate();
            updatePlayerName.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
