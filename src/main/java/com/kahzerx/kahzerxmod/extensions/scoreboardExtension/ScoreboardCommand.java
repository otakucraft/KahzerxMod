package com.kahzerx.kahzerxmod.extensions.scoreboardExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ScoreboardCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ScoreboardExtension scoreboard) {
        dispatcher.register(literal("sb").
                requires(server -> scoreboard.extensionSettings().isEnabled()).
                then(literal("broken").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "broken")))).
                then(literal("crafted").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "crafted")))).
                then(literal("mined").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "mined")))).
                then(literal("used").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "used")))).
                then(literal("picked_up").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "picked_up")))).
                then(literal("dropped").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "dropped")))).
                then(literal("remove").
                        executes(context -> scoreboard.hideSidebar(context.getSource()))));
    }
}
