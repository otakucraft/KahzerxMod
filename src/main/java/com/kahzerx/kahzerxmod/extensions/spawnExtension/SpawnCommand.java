package com.kahzerx.kahzerxmod.extensions.spawnExtension;

import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SpawnExtension spawnExtension) {
        dispatcher.register(literal("spawn").
                requires(server -> spawnExtension.extensionSettings().isEnabled()).
                executes(context -> {
                    SpawnPos pos = spawnExtension.getSpawnPos();
                    if (pos.isValidPos()) {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        player.teleport(DimUtils.getWorld(pos.getDim(), context.getSource().getServer()), pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());
                    } else {
                        context.getSource().sendFeedback(new LiteralText("No está configurado aun... Contacta con un admin."), false);
                    }
                    return 1;
                }));
        dispatcher.register(literal("setSpawn").
                requires(server -> spawnExtension.extensionSettings().isEnabled() && server.hasPermissionLevel(2)).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    BlockPos pos = new BlockPos(player.getX(), player.getY(), player.getZ());
                    spawnExtension.updateSpawnPos(pos, context.getSource());
                    return 1;
                }).
                then(argument("coords", BlockPosArgumentType.blockPos()).
                        executes(context -> spawnExtension.updateSpawnPos(BlockPosArgumentType.getBlockPos(context, "coords"), context.getSource()))));
    }
}
