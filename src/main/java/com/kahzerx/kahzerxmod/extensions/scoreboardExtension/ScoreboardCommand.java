package com.kahzerx.kahzerxmod.extensions.scoreboardExtension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ScoreboardCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, ScoreboardExtension scoreboard) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("sb").requires(server -> scoreboard.extensionSettings().isEnabled());
        getSubCommands(command, commandRegistryAccess, scoreboard, false);
        LiteralArgumentBuilder<ServerCommandSource> persistentSB = literal("persistent");
        getSubCommands(persistentSB, commandRegistryAccess, scoreboard, true);
        command.then(persistentSB);
        dispatcher.register(command);
    }

    public void getSubCommands(LiteralArgumentBuilder<ServerCommandSource> command, CommandRegistryAccess commandRegistryAccess, ScoreboardExtension scoreboard, boolean persistent) {
        command.
                then(literal("broken").
                        then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "broken", persistent)))).
                then(literal("crafted").
                        then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "crafted", persistent)))).
                then(literal("mined").
                        then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "mined", persistent)))).
                then(literal("used").
                        then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "used", persistent)))).
                then(literal("picked_up").
                        then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "picked_up", persistent)))).
                then(literal("dropped").
                        then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "dropped", persistent)))).
                then(literal("deaths").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.deaths", "deaths", "scoreboard objectives add K.deaths deathCount", context.getSource(), Stats.DEATHS, persistent))).
                then(literal("remove").
                        executes(context -> scoreboard.hideSidebar(context.getSource())));
    }
}
