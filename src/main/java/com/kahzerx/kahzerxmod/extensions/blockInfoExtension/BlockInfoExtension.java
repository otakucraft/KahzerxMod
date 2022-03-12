package com.kahzerx.kahzerxmod.extensions.blockInfoExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.database.BlockInfoDatabase;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers.ActionTypes;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers.BlockActionLog;
import com.kahzerx.kahzerxmod.utils.DateUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockInfoExtension extends GenericExtension implements Extensions {
    public BlockInfoDatabase db = new BlockInfoDatabase();

    public static final ArrayBlockingQueue<BlockActionLog> queue = new ArrayBlockingQueue<>(10_000);
    private BlockInfoLoggerThread logger = new BlockInfoLoggerThread("BLOCKINFO", this);
    public BlockInfoExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onCreateDatabase(String worldPath) {
        db = new BlockInfoDatabase();
        db.initializeConnection(worldPath);
        db.getQuery().onCreateDatabase();
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (this.getSettings().isEnabled()) {
            logger.start();
        }
    }

    @Override
    public void onPlayerBreakBlock(ServerPlayerEntity player, World world, BlockPos pos) {
        enqueue(new BlockActionLog(
                player.getName().getString(),
                1,
                world.getBlockState(pos).getBlock().getName().getString(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                DimUtils.getWorldID(DimUtils.getDim(world)),
                ActionTypes.BREAK.getId(),
                DateUtils.getDate()
        ));
    }

    @Override
    public void onPlayerPlaceBlock(ServerPlayerEntity player, World world, BlockPos pos) {
        enqueue(new BlockActionLog(
                player.getName().getString(),
                1,
                world.getBlockState(pos).getBlock().getName().getString(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                DimUtils.getWorldID(DimUtils.getDim(world)),
                ActionTypes.PLACE.getId(),
                DateUtils.getDate()
        ));
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new BlockInfoCommand().register(dispatcher, this);
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
        Extensions.super.onExtensionEnabled();
        if (logger.isAlive()) {
            return;
        }
        queue.clear();
        logger = new BlockInfoLoggerThread("BLOCKINFO", this);
        logger.start();
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        if (!logger.isAlive()) {
            return;
        }
        queue.clear();
        logger.stopLogger();
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
}
