package com.kahzerx.kahzerxmod.extensions.shopExtension;

import com.google.common.collect.Sets;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.shopExtension.bank.BankCommand;
import com.kahzerx.kahzerxmod.extensions.shopExtension.exchange.ExchangeCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ShopExtension extends GenericExtension implements Extensions {
    private final HashMap<ServerPlayerEntity, BankInstance> accounts = new HashMap<>();
    private Connection conn;
    private MinecraftServer server;

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
    public void onServerRun(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        accounts.remove(player);
        BankInstance.Exchanges ex = new BankInstance.Exchanges();
        ex.setDiamond(getAlreadyExchangedItem(player, Items.DIAMOND));
        ex.setDiamondBlock(getAlreadyExchangedItem(player, Items.DIAMOND_BLOCK));
        ex.setNetheriteIngot(getAlreadyExchangedItem(player, Items.NETHERITE_INGOT));
        ex.setNetheriteBlock(getAlreadyExchangedItem(player, Items.NETHERITE_BLOCK));
        ex.setNetheriteScrap(getAlreadyExchangedItem(player, Items.NETHERITE_SCRAP));
        ex.setDebris(getAlreadyExchangedItem(player, Items.ANCIENT_DEBRIS));
        accounts.put(player, new BankInstance(getBalance(player), ex, getTransfers(player)));
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        accounts.remove(player);
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        accounts.clear();
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.onPlayerJoined(player);
        }
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

            String createExchangedItems = "CREATE TABLE IF NOT EXISTS `exchanges`(" +
                    "`uuid` VARCHAR(50) NOT NULL," +
                    "`item` VARCHAR(50) NOT NULL," +
                    "`amount` INTEGER NOT NULL DEFAULT 0);";
            stmt.executeUpdate(createExchangedItems);

            String createTransferredCoins = "CREATE TABLE IF NOT EXISTS `transfers`(" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`uuid` VARCHAR(50) NOT NULL," +
                    "`dest_uuid` VARCHAR(50) NOT NULL," +
                    "`amount` INTEGER NOT NULL DEFAULT 0);";
            stmt.executeUpdate(createTransferredCoins);
            stmt.close();
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
                accounts.get(player).getExchanges().setFromItem(item, amount);
            } else {
                String query = "UPDATE exchanges SET amount = ? WHERE uuid = ? AND item = ?;";
                PreparedStatement ps = conn.prepareStatement(query);
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

    public BankInstance.Transfers getTransfers(ServerPlayerEntity player) {
        BankInstance.Transfers transfers = new BankInstance.Transfers();
        try {
            String query = "SELECT uuid, dest_uuid, amount FROM transfers WHERE uuid = ? OR dest_uuid = ? ORDER BY id DESC LIMIT 9;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, player.getUuidAsString());
            ps.setString(1, player.getUuidAsString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("dest_uuid").equals(player.getUuidAsString())) {
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
            for (ServerPlayerEntity player : accounts.keySet()) {
                if (player.getUuidAsString().equals(playerUUID)) {
                    accounts.get(player).setCoins(actualBalance + amount);
                }
            }
        } catch (SQLException ignored) { }
    }

    public void logTransfer(ServerPlayerEntity player, String destPlayerUUID, String destPlayerName, int amount) {
        try {
            String q = "INSERT INTO transfers(uuid, dest_uuid, amount)" +
                    "VALUES (?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, player.getUuidAsString());
            ps.setString(2, destPlayerUUID);
            ps.setInt(3, amount);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ignored) { }
        accounts.get(player).getTransfers().addTransfer(new BankInstance.Transfer(destPlayerName, amount, false));
        MinecraftServer server = player.getServer();
        if (server != null) {
            ServerPlayerEntity destPlayer = server.getPlayerManager().getPlayer(UUID.fromString(destPlayerUUID));
            if (destPlayer != null) {
                accounts.get(destPlayer).getTransfers().addTransfer(new BankInstance.Transfer(player.getName().getString(), amount, true));
            }
        }
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

    public String getPlayerName(String uuid) {
        String name = "";
        try {
            String q = "SELECT name FROM player WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(q);
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

    public HashMap<ServerPlayerEntity, BankInstance> getAccounts() {
        return accounts;
    }
}
