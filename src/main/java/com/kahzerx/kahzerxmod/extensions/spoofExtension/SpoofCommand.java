package com.kahzerx.kahzerxmod.extensions.spoofExtension;

import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.literal;

public class SpoofCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SpoofExtension spoof) {
        dispatcher.register(literal("spoof").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.argument("player", StringArgumentType.word()).
                        suggests((c, b) -> suggestMatching(PlayerUtils.getPlayers(c.getSource()), b)).
                        then(CommandManager.literal("enderChest").
                                executes(context -> spoof.spoofEC(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")))).
                        then(CommandManager.literal("inventory").
                                executes(context -> spoof.spoofInv(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player"))))));
    }
}
