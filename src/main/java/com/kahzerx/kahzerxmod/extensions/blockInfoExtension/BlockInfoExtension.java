package com.kahzerx.kahzerxmod.extensions.blockInfoExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.BlockInfoUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockInfoExtension extends GenericExtension implements Extensions {
    private Connection conn;
    public static final ArrayBlockingQueue<BlockActionLog> queue = new ArrayBlockingQueue<>(10_000);
    private final BlockInfoLoggerThread logger = new BlockInfoLoggerThread("BLOCKINFO", this);
    public BlockInfoExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onCreateDatabase(Connection conn) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        this.conn = conn;
        try {
            String createBlockInfoTable = "CREATE TABLE IF NOT EXISTS `action_logger` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`playerName` TEXT(20) NOT NULL," +
                    "`block` TEXT(30) NOT NULL," +
                    "`posX` NUMERIC NOT NULL," +
                    "`posY` NUMERIC NOT NULL," +
                    "`posZ` NUMERIC NOT NULL," +
                    "`dim` NUMERIC NOT NULL," +
                    "`action` NUMERIC(3) NOT NULL," +
                    "`date` TEXT NOT NULL);";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createBlockInfoTable);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logAction(BlockActionLog action) {
        String q = "INSERT INTO `action_logger` (playerName, block, posX, posY, posZ, dim, action, date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, action.getPlayer());
            ps.setString(2, action.getBlock());
            ps.setInt(3, action.getX());
            ps.setInt(4, action.getY());
            ps.setInt(5, action.getZ());
            ps.setInt(6, action.getDim());
            ps.setInt(7, action.getActionType());
            ps.setString(8, action.getDate());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void clearLogTable() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS rows FROM `action_logger`;");
            if (rs.next()) {
                if (rs.getInt("rows") > Math.pow(10, 8)) {
                    String deleteRows = "DELETE FROM `action_logger` WHERE id IN (SELECT id FROM `action_logger` LIMIT POWER(10, 7));";
                    stmt.executeUpdate(deleteRows);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (this.getSettings().isEnabled()) {
            logger.start();
        }
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (this.getSettings().isEnabled()) {
            new BlockInfoCommand().register(dispatcher, this);
        }
    }

    @Override
    public void onServerStop() {
        if (this.getSettings().isEnabled()) {
            try {
                logger.stopLogger();
                logger.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAutoSave() {
        if (this.getSettings().isEnabled()) {
            logger.setShouldClear(true);
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onExtensionEnabled() {

    }

    @Override
    public void onExtensionDisabled() {

    }

    public static void enqueue(BlockActionLog action) {
        if (queue.remainingCapacity() > 0) {
            try {
                queue.put(action);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<MutableText> getBlockInfo(int x, int y, int z, int dim, int page) {
        List<MutableText> msg = new ArrayList<>();
        try {
            String q = "SELECT action, date, playerName, block FROM `action_logger` " +
                    "WHERE posX = ? AND posY = ? AND posZ = ? AND dim = ? ORDER BY id DESC LIMIT 10 OFFSET ?;";
            PreparedStatement ps = this.conn.prepareStatement(q);
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setInt(4, dim);
            ps.setInt(5, (page - 1) * 10);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next() && i <= 10) {
                MutableText line = BlockInfoUtils.buildLine(rs);
                msg.add(line);
                i++;
            }
            if (msg.isEmpty()) {
                msg.add(new LiteralText("No hay registros de este bloque :("));
            }
            rs.close();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            msg.clear();
            msg.add(new LiteralText("Error al conectar con la base de datos."));
        }
        return msg;
    }

    public int getLines(int x, int y, int z, int dim) {
        int lines = 0;
        try {
            String q = "SELECT (COUNT(id) / 10) + 1 AS line FROM `action_logger` " +
                    "WHERE posX = ? AND posY = ? AND posZ = ? AND dim = ?;";
            PreparedStatement ps = this.conn.prepareStatement(q);
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setInt(4, dim);
            ResultSet rs = ps.executeQuery();
            lines = rs.getInt("line");
            rs.close();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lines;
    }

    public int getInfo(ServerCommandSource source, BlockPos pos, int page) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        List<MutableText> msg = getBlockInfo(x, y, z, DimUtils.getWorldID(DimUtils.getDim(source.getWorld())), page);
        Collections.reverse(msg);
        source.sendFeedback(new LiteralText("======================"), false);
        int nLine = getLines(x, y, z, DimUtils.getWorldID(DimUtils.getDim(source.getWorld())));

        for (MutableText line : msg) {
            source.sendFeedback(line, false);
        }

        if (page > nLine) {  // No pages.
            return 1;
        } else if (page == nLine && page == 1) {  // There is only 1 page.
            source.sendFeedback(new LiteralText(String.format("%d/%d.", page, nLine)), false);
        } else if (page == 1) {  // First page but there are more
            MutableText pages = BlockInfoUtils.getPages(page, nLine);
            MutableText next = BlockInfoUtils.getNext(x, y, z, page);
            source.sendFeedback(new LiteralText("").append(pages).append(next).append(BlockInfoUtils.getHelp(x, y, z)), false);
        } else if (page == nLine) {  // The last page.
            MutableText prev = BlockInfoUtils.getPrev(x, y, z, page);
            MutableText pages = BlockInfoUtils.getPages(page, nLine);
            source.sendFeedback(new LiteralText("").append(prev).append(pages).append(BlockInfoUtils.getHelp(x, y, z)), false);
        } else {  // Have pages before and after the one you are in.
            MutableText prev = BlockInfoUtils.getPrev(x, y, z, page);
            MutableText pages = BlockInfoUtils.getPages(page, nLine);
            MutableText next = BlockInfoUtils.getNext(x, y, z, page);
            source.sendFeedback(new LiteralText("").append(prev).append(pages).append(next).append(BlockInfoUtils.getHelp(x, y, z)), false);
        }
        return 1;
    }
}
