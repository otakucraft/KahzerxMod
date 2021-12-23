package com.kahzerx.kahzerxmod.extensions.prankExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.HashMap;

public class PrankExtension extends GenericExtension implements Extensions {
    public static boolean isExtensionEnabled = false;
    public static final HashMap<String, PrankLevel> playerLevel = new HashMap<>();
    private Connection conn;
    private MinecraftServer server;

    public PrankExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createBackDatabase = "CREATE TABLE IF NOT EXISTS `prank` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`id` NUMERIC DEFAULT NULL," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid));";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createBackDatabase);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        isExtensionEnabled = this.getSettings().isEnabled();
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        playerLevel.clear();
        isExtensionEnabled = false;
        updatePlayerList();
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.onPlayerJoined(player);
        }
        isExtensionEnabled = true;
        updatePlayerList();
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        playerLevel.remove(playerUUID);
        playerLevel.put(playerUUID, getPlayerLevel(player));
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        playerLevel.remove(playerUUID);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new PrankCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    private PrankLevel getPlayerLevel(ServerPlayerEntity player) {
        try {
            String query = "SELECT id FROM prank WHERE uuid = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, player.getUuidAsString());
            ResultSet rs = ps.executeQuery();
            int level = rs.getInt("id");
            rs.close();
            ps.close();
            return PrankLevel.idToLevel(level);
        } catch (SQLException s) {
            return PrankLevel.idToLevel(0);
        }
    }

    public void updateLevel(ServerPlayerEntity player, PrankLevel level) {
        try {
            String q = "INSERT INTO `prank` (uuid, id)" +
                    "VALUES (?, ?)" +
                    "ON CONFLICT (uuid)" +
                    "DO UPDATE SET id = ?;";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, player.getUuidAsString());
            ps.setInt(2, PrankLevel.levelToID(level));
            ps.setInt(3, PrankLevel.levelToID(level));
            ps.executeUpdate();
            ps.close();
            this.onPlayerJoined(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        updatePlayerList();
    }

    private void updatePlayerList() {
        server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, server.getPlayerManager().getPlayerList()));
    }
}
