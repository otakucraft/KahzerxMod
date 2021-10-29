package com.kahzerx.kahzerxmod.extensions.spawnExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

import java.sql.*;

public class SpawnExtension extends GenericExtension implements Extensions {
    private Connection conn;
    public SpawnExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new SpawnCommand().register(dispatcher, this);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        this.conn = conn;
        try {
            String createDB = "CREATE TABLE IF NOT EXISTS `spawn` (" +
                    "`x` NUMERIC DEFAULT NULL," +
                    "`y` NUMERIC DEFAULT NULL," +
                    "`z` NUMERIC DEFAULT NULL," +
                    "`dim` NUMERIC DEFAULT NULL);";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createDB);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public void deleteSpawn() {
        try {
            String deleteSpawn = "DELETE FROM spawn;";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteSpawn);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SpawnPos getSpawnPos() {
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        int dim = -1;
        try {
            String getSpawn = "SELECT x, y, z, dim FROM spawn;";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getSpawn);
            if (rs.next()) {
                x = rs.getDouble("x");
                y = rs.getDouble("y");
                z = rs.getDouble("z");
                dim = rs.getInt("dim");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return new SpawnPos(0.0D, 0.0D, 0.0D, -1);
        }
        return new SpawnPos(x, y, z, dim);
    }

    public int updateSpawnPos(BlockPos pos, ServerCommandSource source) {
        try {
            ServerPlayerEntity player = source.getPlayer();
            deleteSpawn();
            String updateSpawnPos = "INSERT INTO spawn(x, y, z, dim) VALUES (?, ?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(updateSpawnPos);
            ps.setDouble(1, pos.getX());
            ps.setDouble(2, pos.getY());
            ps.setDouble(3, pos.getZ());
            ps.setInt(4, DimUtils.getWorldID(DimUtils.getDim(player.world)));
            ps.executeUpdate();
            ps.close();
            source.sendFeedback(new LiteralText(
                    String.format(
                            "Spawn en: %s %s",
                            DimUtils.getDimensionWithColor(player.world),
                            DimUtils.formatCoords(pos.getX(), pos.getY(), pos.getZ())
                    )
            ), false);
        } catch (SQLException | CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
