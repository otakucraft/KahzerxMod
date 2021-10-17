package com.kahzerx.kahzerxmod.extensions.permsExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.sql.*;
import java.util.HashMap;

public class PermsExtension extends GenericExtension implements Extensions {
    private final HashMap<String, PermsLevels> playerPerms = new HashMap<>();
    private Connection conn;

    public PermsExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new PermsCommand().register(dispatcher, this);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createBackDatabase = "CREATE TABLE IF NOT EXISTS `perms` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`level` NUMERIC DEFAULT 1," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid));";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createBackDatabase);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        try {
            String query = "INSERT OR IGNORE INTO perms(uuid, level) VALUES (?, ?);";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, playerUUID);
            ps.setInt(2, PermsLevels.MEMBER.getId());
            ps.executeUpdate();
            ps.close();

            if (this.playerPerms.containsKey(playerUUID)) {
                playerPerms.remove(playerUUID);
            }
            playerPerms.put(playerUUID, getDBPlayerPerms(playerUUID));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        if (this.playerPerms.containsKey(playerUUID)) {
            playerPerms.remove(playerUUID);
        }
    }

    public PermsLevels getDBPlayerPerms(String playerUUID) {
        try {
            String query = "SELECT level FROM perms WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, playerUUID);
            ResultSet rs = ps.executeQuery();
            int level = -1;
            if (rs.next()) {
                level = rs.getInt("level");
            }
            rs.close();
            ps.close();
            if (level != -1) {
                return PermsLevels.getValue(level);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return PermsLevels.MEMBER;
    }

    public int updatePerms(ServerCommandSource source, String player, int value) {
        ServerPlayerEntity playerEntity = source.getServer().getPlayerManager().getPlayer(player);
        if (playerEntity == null) {
            return 1;
        }
        try {
            String query = "INSERT INTO perms (uuid, level) VALUES (?, ?)" +
                    "ON CONFLICT (uuid) DO UPDATE SET level = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, playerEntity.getUuidAsString());
            ps.setInt(2, value);
            ps.setInt(3, value);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText(String.format("Player %s > %d", player, value)), false);
            if (this.playerPerms.containsKey(playerEntity.getUuidAsString())) {
                playerPerms.remove(playerEntity.getUuidAsString());
            }
            playerPerms.put(playerEntity.getUuidAsString(), getDBPlayerPerms(playerEntity.getUuidAsString()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        source.getServer().send(new ServerTask(source.getServer().getTicks(), () -> source.getServer().getCommandManager().sendCommandTree(playerEntity)));

        return 1;
    }

    public HashMap<String, PermsLevels> getPlayerPerms() {
        return playerPerms;
    }
}
