package com.kahzerx.kahzerxmod.extensions.shopExtension.database;

import com.google.common.collect.Sets;
import com.kahzerx.kahzerxmod.extensions.shopExtension.BankInstance;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public record BankQuery(Connection connection) {
    public void onCreateDatabase() {
        try {
            Statement stmt = connection.createStatement();
            String createPlayer = "CREATE TABLE IF NOT EXISTS `player` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`name` VARCHAR(50) DEFAULT NULL);";
            stmt.executeUpdate(createPlayer);

            String createBAccDatabase = "CREATE TABLE IF NOT EXISTS `bank_account` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`money` NUMERIC DEFAULT 0," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid));";
            stmt.executeUpdate(createBAccDatabase);

            String createExchangedItems = "CREATE TABLE IF NOT EXISTS `exchanges`(" +
                    "`uuid` VARCHAR(50) NOT NULL," +
                    "`item` VARCHAR(50) NOT NULL," +
                    "`amount` INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY (uuid, item)," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid));";
            stmt.executeUpdate(createExchangedItems);

            String createTransferredCoins = "CREATE TABLE IF NOT EXISTS `transfers`(" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`uuid` VARCHAR(50) NOT NULL," +
                    "`dest_uuid` VARCHAR(50) NOT NULL," +
                    "`amount` INTEGER NOT NULL DEFAULT 0," +
                    "`date` DATETIME NOT NULL);";
            stmt.executeUpdate(createTransferredCoins);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logExchange(ServerPlayerEntity player, Item item, int amount, HashMap<ServerPlayerEntity, BankInstance> accounts) {
        try {
            int prevAmount = getAlreadyExchangedItem(player, item);
            if (prevAmount == -1) {
                String query = "INSERT INTO exchanges(uuid, item, amount) VALUES (?, ?, ?);";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, player.getUuidAsString());
                ps.setString(2, item.getTranslationKey());
                ps.setInt(3, amount);
                ps.executeUpdate();
                ps.close();
                accounts.get(player).getExchanges().setFromItem(item, amount);
            } else {
                String query = "UPDATE exchanges SET amount = ? WHERE uuid = ? AND item = ?;";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, prevAmount + amount);
                ps.setString(2, player.getUuidAsString());
                ps.setString(3, item.getTranslationKey());
                ps.executeUpdate();
                ps.close();
                accounts.get(player).getExchanges().setFromItem(item, prevAmount + amount);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public int getAlreadyExchangedItem(ServerPlayerEntity player, Item item) {
        try {
            String query = "SELECT amount FROM exchanges WHERE uuid = ? AND item = ?;";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, player.getUuidAsString());
            ps.setString(2, item.getTranslationKey());
            int amount = -1;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                amount = rs.getInt("amount");
            }
            rs.close();
            ps.close();
            return amount;
        } catch (SQLException s) {
            s.printStackTrace();
            return -1;
        }
    }

    public BankInstance.Transfers getTransfers(ServerPlayerEntity player, int page) {
        return getTransfers(player.getUuidAsString(), page);
    }

    public BankInstance.Transfers getTransfers(String uuid, int page) {
        BankInstance.Transfers transfers = new BankInstance.Transfers();
        try {
            String query = "SELECT uuid, dest_uuid, amount FROM transfers WHERE uuid = ? OR dest_uuid = ? ORDER BY id DESC LIMIT 9 OFFSET ?;";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            ps.setString(2, uuid);
            ps.setInt(3, (page - 1) * 9);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("dest_uuid").equals(uuid)) {
                    BankInstance.Transfer t = new BankInstance.Transfer(getPlayerName(rs.getString("uuid")), rs.getInt("amount"), true);
                    transfers.addTransfer(t);
                } else {
                    BankInstance.Transfer t = new BankInstance.Transfer(getPlayerName(rs.getString("dest_uuid")), rs.getInt("amount"), false);
                    transfers.addTransfer(t);
                }
            }
            transfers.revert();
            rs.close();
            ps.close();
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return transfers;
    }

    public int getBalance(ServerPlayerEntity player) {
        return getBalance(player.getUuidAsString());
    }

    public int getBalance(String playerUUID) {
        try {
            String query = "SELECT money FROM bank_account WHERE uuid = ?;";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, playerUUID);
            ResultSet rs = ps.executeQuery();
            int money = 0;
            if (rs.next()) {
                money = rs.getInt("money");
            }
            rs.close();
            ps.close();
            return money;
        } catch (SQLException s) {
            return 0;
        }
    }

    public void updateFounds(ServerPlayerEntity player, int amount, HashMap<ServerPlayerEntity, BankInstance> accounts) {
        updateFounds(player.getUuidAsString(), amount, accounts);
    }

    public void updateFounds(String playerUUID, int amount, HashMap<ServerPlayerEntity, BankInstance> accounts) {
        int actualBalance = getBalance(playerUUID);
        try {
            String q = "INSERT INTO bank_account(uuid, money)" +
                    "VALUES (?, ?)" +
                    "ON CONFLICT (uuid) " +
                    "DO UPDATE SET money = ?;";
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setString(1, playerUUID);
            ps.setInt(2, actualBalance + amount);
            ps.setInt(3, actualBalance + amount);
            ps.executeUpdate();
            ps.close();
            for (ServerPlayerEntity player : accounts.keySet()) {
                if (player.getUuidAsString().equals(playerUUID)) {
                    accounts.get(player).setCoins(actualBalance + amount);
                }
            }
        } catch (SQLException ignored) { }
    }

    public void logTransfer(ServerPlayerEntity player, String destPlayerUUID, String destPlayerName, int amount, HashMap<ServerPlayerEntity, BankInstance> accounts) {
        logTransfer(player.getUuidAsString(), destPlayerUUID, destPlayerName, amount, accounts);
    }

    public void logTransfer(String playerUUID, String destPlayerUUID, String destPlayerName, int amount, HashMap<ServerPlayerEntity, BankInstance> accounts) {
        try {
            String q = "INSERT INTO transfers(uuid, dest_uuid, amount, date)" +
                    "VALUES (?, ?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setString(1, playerUUID);
            ps.setString(2, destPlayerUUID);
            ps.setInt(3, amount);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ignored) { }
        for (ServerPlayerEntity player : accounts.keySet()) {
            if (player.getUuidAsString().equals(playerUUID)) {
                accounts.get(player).getTransfers().addTransfer(new BankInstance.Transfer(destPlayerName, amount, false));
                MinecraftServer server = player.getServer();
                if (server != null) {
                    ServerPlayerEntity destPlayer = server.getPlayerManager().getPlayer(UUID.fromString(destPlayerUUID));
                    if (destPlayer != null) {
                        accounts.get(destPlayer).getTransfers().addTransfer(new BankInstance.Transfer(player.getName().getString(), amount, true));
                    }
                }
            }
        }
    }

    public Collection<String> getPlayers() {
        Set<String> players = Sets.newLinkedHashSet();
        try {
            String query = "SELECT name FROM player;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                players.add(rs.getString("name"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public String getPlayerUUID(String name) {
        String playerUUID = null;
        try {
            String query = "SELECT uuid FROM player WHERE name = ?;";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                playerUUID = rs.getString("uuid");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerUUID;
    }

    public String getPlayerName(String uuid) {
        String name = "";
        try {
            String q = "SELECT name FROM player WHERE uuid = ?;";
            PreparedStatement ps = connection.prepareStatement(q);
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
            rs.close();
            ps.close();
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return name;
    }

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
