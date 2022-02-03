package com.kahzerx.kahzerxmod.extensions.homeExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.sql.*;
import java.util.HashMap;

public class HomeExtension extends GenericExtension implements Extensions {
    private final HashMap<String, HomePos> playerHomes = new HashMap<>();
    private Connection conn;
    private MinecraftServer server;

    public HomeExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createBackDatabase = "CREATE TABLE IF NOT EXISTS `home` (" +
                    "`uuid` VARCHAR(50) PRIMARY KEY NOT NULL," +
                    "`homeX` NUMERIC DEFAULT NULL," +
                    "`homeY` NUMERIC DEFAULT NULL," +
                    "`homeZ` NUMERIC DEFAULT NULL," +
                    "`homeDim` NUMERIC DEFAULT NULL," +
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
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        playerHomes.remove(playerUUID);
        playerHomes.put(playerUUID, getHomePos(player));
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        playerHomes.remove(playerUUID);
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        playerHomes.clear();
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.onPlayerJoined(player);
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new HomeCommand().register(dispatcher, this);
        new SetHomeCommand().register(dispatcher, this);
    }

    private void updateHomePos(ServerPlayerEntity player, HomePos pos) {
        try {
            String q = "INSERT INTO `home` (uuid, homeX, homeY, homeZ, homeDim)" +
                    "VALUES (?, ?, ?, ?, ?)" +
                    "ON CONFLICT (uuid)" +
                    "DO UPDATE SET homeX = ?, homeY = ?, homeZ = ?, homeDim = ?;";
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

    private HomePos getHomePos(ServerPlayerEntity player) {
        try {
            String query = "SELECT homeX, homeY, homeZ, homeDim FROM home WHERE uuid = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, player.getUuidAsString());
            ResultSet rs = ps.executeQuery();
            double x = rs.getDouble("homeX");
            double y = rs.getDouble("homeY");
            double z = rs.getDouble("homeZ");
            int dim = rs.getInt("homeDim");
            rs.close();
            ps.close();
            return new HomePos(x, y, z, DimUtils.getWorldString(dim));
        } catch (SQLException s) {
            return new HomePos(0, 0, 0, "");
        }
    }

    public int tpHome(ServerCommandSource src) throws CommandSyntaxException {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            return 1;
        }
        String playerUUID = player.getUuidAsString();
        HomePos homePos = playerHomes.get(playerUUID);
        if (homePos.isValid()) {
            player.teleport(
                    DimUtils.getWorld(homePos.getDim(), player),
                    homePos.getX(),
                    homePos.getY(),
                    homePos.getZ(),
                    player.getYaw(),
                    player.getPitch()
            );
            player.addExperience(0);  // xp resets when you tp from other dimension and needs to update smh, mojang pls.
        } else {
            player.sendMessage(MarkEnum.INFO.appendText(new LiteralText("You don't have a home yet, use ").styled(style -> style.withColor(Formatting.WHITE)).append(getClickableSetHomeCommand())), false);
        }
        return 1;
    }

    public int saveHome(ServerCommandSource src) throws CommandSyntaxException {
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            return 1;
        }
        HomePos newHomePos = new HomePos(player.getX(), player.getY(), player.getZ(), DimUtils.getDim(player.world));
        String playerUUID = player.getUuidAsString();
        playerHomes.put(playerUUID, newHomePos);
        src.sendFeedback(
            new LiteralText(String.format(
                "Home @ %s %s",
                DimUtils.getDimensionWithColor(player.world),
                DimUtils.formatCoords(player.getX(), player.getY(), player.getZ())
            )),
            false
        );
        updateHomePos(player, newHomePos);
        return 1;
    }

    public static MutableText getClickableSetHomeCommand() {
        return new LiteralText("/setHome").styled((style -> style.withColor(Formatting.DARK_GREEN).
                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setHome")).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("setHome")))));
    }
}
