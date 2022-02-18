package com.kahzerx.kahzerxmod.extensions.shopExtension;

import com.google.common.collect.Sets;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.shopExtension.bank.BankCommand;
import com.kahzerx.kahzerxmod.extensions.shopExtension.exchange.ExchangeCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.Collection;
import java.util.Set;

public class ShopExtension extends GenericExtension implements Extensions {
    private Connection conn;

    public ShopExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new ExchangeCommand().register(dispatcher, this);
        new BankCommand().register(dispatcher, this);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createBAccDatabase = "CREATE TABLE IF NOT EXISTS `bank_account` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`money` NUMERIC DEFAULT 0," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid));";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createBAccDatabase);
            stmt.close();

            String createExchangedItems = "CREATE TABLE IF NOT EXISTS `exchanges`(" +
                    "`uuid` VARCHAR(50) NOT NULL," +
                    "`item` VARCHAR(50) NOT NULL," +
                    "`amount` INTEGER NOT NULL DEFAULT 0);";
            Statement stmt1 = conn.createStatement();
            stmt1.executeUpdate(createExchangedItems);
            stmt1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logExchange(ServerPlayerEntity player, Item item, int amount) {
        try {
            int prevAmount = getAlreadyExchangedItem(player, item);
            if (prevAmount == -1) {
                String query = "INSERT INTO exchanges(uuid, item, amount) VALUES (?, ?, ?);";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, player.getUuidAsString());
                ps.setString(2, item.getTranslationKey());
                ps.setInt(3, amount);
                ps.executeUpdate();
                ps.close();
            } else {
                String query = "UPDATE exchanges SET amount = ? WHERE uuid = ? AND item = ?;";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, prevAmount + amount);
                ps.setString(2, player.getUuidAsString());
                ps.setString(3, item.getTranslationKey());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public int getAlreadyExchangedItem(ServerPlayerEntity player, Item item) {
        try {
            String query = "SELECT amount FROM exchanges WHERE uuid = ? AND item = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
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

    public int getBalance(ServerPlayerEntity player) {
        return getBalance(player.getUuidAsString());
    }

    public int getBalance(String playerUUID) {
        try {
            String query = "SELECT money FROM bank_account WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
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

    public void updateFounds(ServerPlayerEntity player, int amount) {
        updateFounds(player.getUuidAsString(), amount);
    }

    public void updateFounds(String playerUUID, int amount) {
        int actualBalance = getBalance(playerUUID);
        try {
            String q = "INSERT INTO bank_account(uuid, money)" +
                    "VALUES (?, ?)" +
                    "ON CONFLICT (uuid) " +
                    "DO UPDATE SET money = ?;";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, playerUUID);
            ps.setInt(2, actualBalance + amount);
            ps.setInt(3, actualBalance + amount);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ignored) { }
    }

    public Collection<String> getPlayers() {
        Set<String> players = Sets.newLinkedHashSet();
        try {
            String query = "SELECT name FROM player;";
            Statement stmt = conn.createStatement();
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
            PreparedStatement ps = conn.prepareStatement(query);
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
}
