package com.kahzerx.kahzerxmod.extensions.hereExtension;

import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, HereExtension here) {
        dispatcher.register(literal("here").
                requires(server -> here.extensionSettings().isEnabled()).
                then(literal("to").
                        then(argument("player", StringArgumentType.word()).
                                suggests((c, b) -> suggestMatching(PlayerUtils.getPlayers(c.getSource()), b)).
                                executes(context -> {
                                    ServerPlayerEntity playerEntity = context.getSource().getServer().getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));
                                    if (playerEntity != null) {
                                        return here.sendLocation(context.getSource(), playerEntity);
                                    }
                                    return 1;
                                }))).
                executes(context -> here.sendLocation(context.getSource(), null)));
    }
}
