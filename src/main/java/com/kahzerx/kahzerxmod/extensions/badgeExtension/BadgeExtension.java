package com.kahzerx.kahzerxmod.extensions.badgeExtension;

import com.google.common.collect.Sets;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.sql.*;
import java.util.*;

public class BadgeExtension extends GenericExtension implements Extensions {
    public final PermsExtension permsExtension;
    private Connection conn;
    public static boolean isExtensionEnabled = false;
    public static final HashMap<String, List<BadgeInstance>> playerBadges = new HashMap<>();
    private MinecraftServer server;

    public BadgeExtension(ExtensionSettings settings, PermsExtension permsExtension) {
        super(settings);
        this.permsExtension = permsExtension;
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        playerBadges.remove(playerUUID);
        playerBadges.put(playerUUID, new ArrayList<>());
        for (String badge : getPlayerBadges(player.getName().getString())) {
            BadgeInstance instance = getBadgeInstance(badge);
            if (instance == null) {
                continue;
            }
            playerBadges.get(playerUUID).add(instance);
        }
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        playerBadges.remove(playerUUID);
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        isExtensionEnabled = this.getSettings().isEnabled();
        this.server = minecraftServer;
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new BadgeCommand().register(dispatcher, this);
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        isExtensionEnabled = true;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.onPlayerJoined(player);
        }
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        isExtensionEnabled = false;
        playerBadges.clear();
    }

    public void reload() {
        if (this.getSettings().isEnabled()) {
            onExtensionDisabled();
            onExtensionEnabled();
        }
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createBadge = "CREATE TABLE IF NOT EXISTS `badges` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`badge` VARCHAR(50) NOT NULL," +
                    "`color` INTEGER NOT NULL DEFAULT 15," +
                    "`description` VARCHAR(50) NOT NULL DEFAULT '');";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createBadge);

            String createBadgeRel = "CREATE TABLE IF NOT EXISTS `badge_player` (" +
                    "badge_id INTEGER NOT NULL," +
                    "uuid VARCHAR(50) NOT NULL," +
                    "FOREIGN KEY(badge_id) REFERENCES badges(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(uuid) REFERENCES player(uuid) ON DELETE CASCADE," +
                    "PRIMARY KEY(badge_id, uuid));";
            stmt.executeUpdate(createBadgeRel);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean badgeExists(String badge) {
        boolean exists = false;
        try {
            String checkBadge = "SELECT id FROM badges WHERE badge = ?;";
            PreparedStatement ps = this.conn.prepareStatement(checkBadge);
            ps.setString(1, badge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private boolean badgeExists(int badgeID) {
        boolean exists = false;
        try {
            String checkBadge = "SELECT id FROM badges WHERE id = ?;";
            PreparedStatement ps = this.conn.prepareStatement(checkBadge);
            ps.setInt(1, badgeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private BadgeInstance getBadgeInstance(String badge) {
        BadgeInstance instance = null;
        try {
            String get = "SELECT color, description FROM badges WHERE badge = ?;";
            PreparedStatement ps = this.conn.prepareStatement(get);
            ps.setString(1, badge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                instance = new BadgeInstance(badge, rs.getInt("color"), rs.getString("description"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private boolean relationExists(int badgeID, String playerUUID) {
        boolean exists = false;
        try {
            String checkBadge = "SELECT uuid FROM badge_player WHERE uuid = ? AND badge_id = ?;";
            PreparedStatement ps = this.conn.prepareStatement(checkBadge);
            ps.setString(1, playerUUID);
            ps.setInt(2, badgeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private String getBadgeNameByID(int badgeID) {
        String name = null;
        try {
            String get = "SELECT badge FROM badges WHERE id = ?;";
            PreparedStatement ps = this.conn.prepareStatement(get);
            ps.setInt(1, badgeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("badge");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public int getBadgeID(String badge) {
        int id = -1;
        try {
            String get = "SELECT id FROM badges WHERE badge = ?;";
            PreparedStatement ps = this.conn.prepareStatement(get);
            ps.setString(1, badge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public Collection<String> getAllBadges() {
        Set<String> badges = Sets.newLinkedHashSet();
        try {
            String get = "SELECT badge FROM badges;";
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(get);
            while (rs.next()) {
                badges.add(rs.getString("badge"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public Collection<String> getPlayerBadges(String playerName) {
        String playerUUID = getPlayerUUID(playerName);
        Set<String> badges = Sets.newLinkedHashSet();
        if (playerUUID == null) {
            return badges;
        }
        try {
            String get = "SELECT badge_id FROM badge_player WHERE uuid = ?;";
            PreparedStatement ps = this.conn.prepareStatement(get);
            ps.setString(1, playerUUID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String badgeName = getBadgeNameByID(rs.getInt("badge_id"));
                if (badgeName != null) {
                    badges.add(badgeName);
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public Collection<String> getNotPlayerBadges(String playerName) {
        Collection<String> allBadges = getAllBadges();
        Collection<String> playerBadges = getPlayerBadges(playerName);
        Set<String> nonPlayerBadges = Sets.newLinkedHashSet();
        for (String badge : allBadges) {
            if (playerBadges.contains(badge)) {
                continue;
            }
            nonPlayerBadges.add(badge);
        }
        return nonPlayerBadges;
    }

    public Collection<String> getAllIDs() {
        Set<String> badgeIDs = Sets.newLinkedHashSet();
        try {
            String get = "SELECT id FROM badges;";
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(get);
            while (rs.next()) {
                badgeIDs.add(String.valueOf(rs.getInt("id")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badgeIDs;
    }

    public Collection<String> getAllPlayers() {
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

    public void getID(ServerCommandSource source, String badge) {
        if (!badgeExists(badge)) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        int id = this.getBadgeID(badge);
        if (id == -1) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
        } else {
            source.sendFeedback(new LiteralText(String.format("> Badge ID is %d", id)).styled(style -> style.withBold(true).withColor(Formatting.WHITE)), false);
        }
    }

    public void insertBadge(ServerCommandSource source, String badge) {
        if (badgeExists(badge)) {
            source.sendFeedback(new LiteralText("✘ Badge already exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        try {
            String insertBadge = "INSERT INTO badges(badge) VALUES (?)";
            PreparedStatement ps = this.conn.prepareStatement(insertBadge);
            ps.setString(1, badge);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText("✓ Badge created!").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), false);
            reload();
        } catch (SQLException e) {
            e.printStackTrace();
            source.sendFeedback(new LiteralText("✘ Error creating badge!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
        }
    }

    public void removeBadge(ServerCommandSource source, String badge) {
        if (!badgeExists(badge)) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        try {
            String removeBadge = "DELETE FROM badges WHERE badge = ?;";
            PreparedStatement ps = this.conn.prepareStatement(removeBadge);
            ps.setString(1, badge);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText("✓ Badge removed!").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), false);
            reload();
        } catch (SQLException e) {
            e.printStackTrace();
            source.sendFeedback(new LiteralText("✘ Error removing badge!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
        }
    }

    public void modifyBadgeColor(ServerCommandSource source, int badgeID, Formatting color) {
        if (!badgeExists(badgeID)) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        try {
            String editColor = "UPDATE badges SET color = ? WHERE id = ?;";
            PreparedStatement ps = this.conn.prepareStatement(editColor);
            ps.setInt(1, color.getColorIndex());
            ps.setInt(2, badgeID);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText("✓ Badge color edited!").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), false);
            reload();
        } catch (SQLException e) {
            e.printStackTrace();
            source.sendFeedback(new LiteralText("✘ Error editing badge color!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
        }
    }

    public void modifyBadgeDesc(ServerCommandSource source, int badgeID, String description) {
        if (!badgeExists(badgeID)) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        try {
            String editDesc = "UPDATE badges SET description = ? WHERE id = ?;";
            PreparedStatement ps = this.conn.prepareStatement(editDesc);
            ps.setString(1, description);
            ps.setInt(2, badgeID);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText("✓ Badge description edited!").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), false);
            reload();
        } catch (SQLException e) {
            e.printStackTrace();
            source.sendFeedback(new LiteralText("✘ Error editing badge desc!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
        }
    }

    public void addBadge(ServerCommandSource source, String badge, String playerName) {
        if (!badgeExists(badge)) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        String playerUUID = getPlayerUUID(playerName);
        if (playerUUID == null) {
            source.sendFeedback(new LiteralText("✘ Player does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        int badgeID = getBadgeID(badge);
        if (relationExists(badgeID, playerUUID)) {
            source.sendFeedback(new LiteralText("✘ Player already has this badge!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        try {
            String insertBadge = "INSERT INTO badge_player(badge_id, uuid) VALUES (?, ?);";
            PreparedStatement ps = this.conn.prepareStatement(insertBadge);
            ps.setInt(1, badgeID);
            ps.setString(2, playerUUID);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText("✓ Badge added!").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), false);
        } catch (SQLException e) {
            e.printStackTrace();
            source.sendFeedback(new LiteralText("✘ Error adding badge!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            reload();
        }
    }

    public void deleteBadge(ServerCommandSource source, String badge, String playerName) {
        String playerUUID = getPlayerUUID(playerName);
        if (playerUUID == null) {
            source.sendFeedback(new LiteralText("✘ Player does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        if (!badgeExists(badge)) {
            source.sendFeedback(new LiteralText("✘ Badge does not exists!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        int badgeID = getBadgeID(badge);
        if (!relationExists(badgeID, playerUUID)) {
            source.sendFeedback(new LiteralText("✘ Player does not have this badge!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
            return;
        }
        try {
            String insertBadge = "DELETE FROM badge_player WHERE badge_id = ? AND uuid = ?;";
            PreparedStatement ps = this.conn.prepareStatement(insertBadge);
            ps.setInt(1, badgeID);
            ps.setString(2, playerUUID);
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText("✓ Badge deleted!").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), false);
            reload();
        } catch (SQLException e) {
            e.printStackTrace();
            source.sendFeedback(new LiteralText("✘ Error deleting badge!").styled(style -> style.withBold(true).withColor(Formatting.DARK_RED)), false);
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }
}
