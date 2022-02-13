package com.kahzerx.kahzerxmod.extensions.whereExtension;

import com.kahzerx.kahzerxmod.extensions.whereExtension.WhereExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WhereCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, WhereExtension where) {
        dispatcher.register(literal("where").
                requires(server -> where.extensionSettings().isEnabled()).
                then(argument("player", StringArgumentType.word()).
                        suggests((c, b) -> suggestMatching(PlayerUtils.getPlayers(c.getSource()), b)).
                        executes(context -> {
                            ServerPlayerEntity playerEntity = context.getSource().getServer().getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));
                            if (playerEntity != null) {
                                context.getSource().sendFeedback(new LiteralText(String.format(
                                        "%s %s %s",
                                        PlayerUtils.getPlayerWithColor(playerEntity),
                                        DimUtils.getDimensionWithColor(playerEntity.world),
                                        DimUtils.formatCoords(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())
                                )), false);
                            }
                            return 1;
                        })));
    }
}
