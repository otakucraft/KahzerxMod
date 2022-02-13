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
                then(literal("persistent").
                        then(literal("broken").
                                then(argument("item", ItemStackArgumentType.itemStack()).
                                        executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "broken", true)))).
                        then(literal("crafted").
                                then(argument("item", ItemStackArgumentType.itemStack()).
                                        executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "crafted", true)))).
                        then(literal("mined").
                                then(argument("item", ItemStackArgumentType.itemStack()).
                                        executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "mined", true)))).
                        then(literal("used").
                                then(argument("item", ItemStackArgumentType.itemStack()).
                                        executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "used", true)))).
                        then(literal("picked_up").
                                then(argument("item", ItemStackArgumentType.itemStack()).
                                        executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "picked_up", true)))).
                        then(literal("dropped").
                                then(argument("item", ItemStackArgumentType.itemStack()).
                                        executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "dropped", true))))).
                then(literal("broken").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "broken", false)))).
                then(literal("crafted").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "crafted", false)))).
                then(literal("mined").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "mined", false)))).
                then(literal("used").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "used", false)))).
                then(literal("picked_up").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "picked_up", false)))).
                then(literal("dropped").
                        then(argument("item", ItemStackArgumentType.itemStack()).
                                executes(context -> scoreboard.startThreadedShowSideBar(context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), "dropped", false)))).
                then(literal("remove").
                        executes(context -> scoreboard.hideSidebar(context.getSource()))));
    }
}
