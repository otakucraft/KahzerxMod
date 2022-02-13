package com.kahzerx.kahzerxmod.extensions.permsExtension;

import com.google.common.collect.Sets;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        Team actual = server.getScoreboard().getPlayerTeam(player.getName().getString());
        Team shouldTeam = server.getScoreboard().getTeam(playerPerms.get(playerUUID).getName());
        if (actual == null || shouldTeam == null) {
            return;
        }
        if (!actual.isEqual(shouldTeam)) {
            server.getScoreboard().addPlayerToTeam(player.getName().getString(), shouldTeam);
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

    private String getPlayerUUID(String name) {
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

    public int updatePerms(ServerCommandSource source, String player, String value) {
        String playerUUID = getPlayerUUID(player);
        if (playerUUID == null) {
            return 1;
        }
        int level = PermsLevels.getLevel(value);
        if (level == -1) {
            source.sendFeedback(MarkEnum.CROSS.appendMessage("Not a valid level!"), false);
            return 1;
        }
        try {
            String query = "INSERT INTO perms (uuid, level) VALUES (?, ?)" +
                    "ON CONFLICT (uuid) DO UPDATE SET level = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, playerUUID);
            ps.setInt(2, level);
            ps.setInt(3, level);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(MarkEnum.TICK.appendMessage(String.format("Player %s > %s", player, value)), false);
            if (this.playerPerms.containsKey(playerUUID)) {
                playerPerms.remove(playerUUID);
            }
            playerPerms.put(playerUUID, getDBPlayerPerms(playerUUID));
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
        PlayerUtils.reloadCommands();

        Collection<String> teamNames = source.getServer().getScoreboard().getTeamNames();
        if (teamNames.contains(PermsLevels.getValue(level).getName())) {
            source.getServer().getScoreboard().addPlayerToTeam(player, source.getServer().getScoreboard().getTeam(PermsLevels.getValue(level).getName()));
        }

        return 1;
    }

    public HashMap<String, PermsLevels> getPlayerPerms() {
        return playerPerms;
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
}
