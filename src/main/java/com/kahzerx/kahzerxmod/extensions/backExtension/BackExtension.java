package com.kahzerx.kahzerxmod.extensions.backExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.sql.*;
import java.util.HashMap;

public class BackExtension extends GenericExtension implements Extensions {
    private final HashMap<String, BackPos> playerBack = new HashMap<>();
    private Connection conn;
    public final PermsExtension permsExtension;

    public BackExtension(ExtensionSettings settings, PermsExtension perms) {
        super(settings);
        this.permsExtension = perms;
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createBackDatabase = "CREATE TABLE IF NOT EXISTS `back` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`deathX` NUMERIC DEFAULT NULL," +
                    "`deathY` NUMERIC DEFAULT NULL," +
                    "`deathZ` NUMERIC DEFAULT NULL," +
                    "`deathDim` NUMERIC DEFAULT NULL," +
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
        if (this.playerBack.containsKey(playerUUID)) {
            playerBack.remove(playerUUID);
        }
        playerBack.put(playerUUID, getDeathPos(player));
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        if (this.playerBack.containsKey(playerUUID)) {
            playerBack.remove(playerUUID);
        }
    }

    @Override
    public void onPlayerDied(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        BackPos backPos = new BackPos(player.getX(), player.getY(), player.getZ(), DimUtils.getDim(player.world));
        playerBack.put(playerUUID, backPos);
        updateDeathPos(player, backPos);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new BackCommand().register(dispatcher, this);
    }

    private void updateDeathPos(ServerPlayerEntity player, BackPos pos) {
        try {
            String q = "INSERT INTO `back` (uuid, deathX, deathY, deathZ, deathDim)" +
                    "VALUES (?, ?, ?, ?, ?)" +
                    "ON CONFLICT (uuid)" +
                    "DO UPDATE SET deathX = ?, deathY = ?, deathZ = ?, deathDim = ?;";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, player.getUuidAsString());
            ps.setDouble(2, pos.getX());
            ps.setDouble(3, pos.getY());
            ps.setDouble(4, pos.getZ());
            ps.setInt(5, DimUtils.getWorldID(pos.dim()));
            ps.setDouble(6, pos.getX());
            ps.setDouble(7, pos.getY());
            ps.setDouble(8, pos.getZ());
            ps.setInt(9, DimUtils.getWorldID(pos.dim()));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private BackPos getDeathPos(ServerPlayerEntity player) {
        try {
            String query = "SELECT deathX, deathY, deathZ, deathDim FROM back WHERE uuid = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, player.getUuidAsString());
            ResultSet rs = ps.executeQuery();
            double x = rs.getDouble("deathX");
            double y = rs.getDouble("deathY");
            double z = rs.getDouble("deathZ");
            int dim = rs.getInt("deathDim");
            rs.close();
            ps.close();
            return new BackPos(x, y, z, DimUtils.getWorldString(dim));
        } catch (SQLException s) {
            return new BackPos(0, 0, 0, "");
        }
    }

    public int tpBack(ServerCommandSource src) throws CommandSyntaxException {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            return 1;
        }
        if (permsExtension.getPlayerPerms().containsKey(player.getUuidAsString())) {
            if (permsExtension.getPlayerPerms().get(player.getUuidAsString()).getId() == PermsLevels.MEMBER.getId()) {
                player.sendMessage(
                        new LiteralText("You don't have permission to run this command."),
                        false
                );
                return 1;
            }
        } else {
            player.sendMessage(
                    new LiteralText("Error."),
                    false
            );
            return 1;
        }
        String playerUUID = player.getUuidAsString();
//        if (player.getVelocity().getY() < -1 || player.isOnFire() || player.isSubmergedInWater() || player.getDamageTracker().wasRecentlyAttacked()) {
//            player.sendMessage(
//                    new LiteralText("NOP").styled(style -> style.withColor(Formatting.DARK_RED)),
//                    false
//            );
//            return 1;
//        }
        if (!this.playerBack.containsKey(playerUUID)) {
            player.sendMessage(
                    new LiteralText("Error."),
                    false
            );
            return 1;
        }
        BackPos backPos = playerBack.get(playerUUID);
        if (backPos.isValid()) {
            player.teleport(
                    DimUtils.getWorld(backPos.getDim(), player),
                    backPos.getX(),
                    backPos.getY(),
                    backPos.getZ(),
                    player.getYaw(),
                    player.getPitch()
            );
            player.addExperience(0);  // xp resets when you tp from other dimension and needs to update smh, mojang pls.
        } else {
            player.sendMessage(
                    new LiteralText("You haven't died yet :("),
                    false
            );
        }
        return 1;
    }
}
