package com.kahzerx.kahzerxmod.extensions.blockInfoExtension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BlockInfoCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, BlockInfoExtension blockInfo) {
        ArgumentBuilder<ServerCommandSource, RequiredArgumentBuilder<ServerCommandSource, PosArgument>> bi = argument("coords", BlockPosArgumentType.blockPos()).
                executes(context -> blockInfo.threadedGetInfo(
                        context.getSource(), BlockPosArgumentType.getBlockPos(context, "coords"), 1)).
                then(argument("int", IntegerArgumentType.integer(1)).
                        executes(context -> blockInfo.threadedGetInfo(
                                context.getSource(),
                                BlockPosArgumentType.getBlockPos(context, "coords"),
                                IntegerArgumentType.getInteger(context, "int"))));
        dispatcher.register(literal("blockInfo").
                requires(server -> blockInfo.extensionSettings().isEnabled()).
                then(bi));
        dispatcher.register(literal("bi").
                requires(server -> blockInfo.extensionSettings().isEnabled()).
                then(bi));
    }
}
