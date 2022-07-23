package com.kahzerx.kahzerxmod.extensions.scoreboardExtension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
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
                then(literal("killed").
                        then(argument("entity", EntitySummonArgumentType.entitySummon()).
                                suggests(SuggestionProviders.SUMMONABLE_ENTITIES).
                                executes(context -> scoreboard.startThreadedKilledScoreboard(context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), "killed", persistent)))).
                then(literal("killed_by").
                        then(argument("entity", EntitySummonArgumentType.entitySummon()).
                                suggests(SuggestionProviders.SUMMONABLE_ENTITIES).
                                executes(context -> scoreboard.startThreadedKilledScoreboard(context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), "killed_by", persistent)))).
                then(literal("deaths").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.deaths", "deaths", "scoreboard objectives add K.deaths deathCount", context.getSource(), Stats.DEATHS, persistent))).
                then(literal("killed_mobs").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.killed_mobs", "killed mobs", "scoreboard objectives add K.killed_mobs minecraft.custom:minecraft.mob_kills", context.getSource(), Stats.MOB_KILLS, persistent))).
                then(literal("cm_aviated").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.cm_aviated", "cm aviated", "scoreboard objectives add K.cm_aviated minecraft.custom:minecraft.aviate_one_cm", context.getSource(), Stats.AVIATE_ONE_CM, persistent))).
                then(literal("cm_pig").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.cm_pig", "cm pig", "scoreboard objectives add K.cm_pig minecraft.custom:minecraft.pig_one_cm", context.getSource(), Stats.PIG_ONE_CM, persistent))).
                then(literal("cm_swim").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.cm_swim", "cm swim", "scoreboard objectives add K.cm_swim minecraft.custom:minecraft.swim_one_cm", context.getSource(), Stats.SWIM_ONE_CM, persistent))).
                then(literal("jump").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.jump", "jump", "scoreboard objectives add K.jump minecraft.custom:minecraft.jump", context.getSource(), Stats.JUMP, persistent))).
                then(literal("pot_flower").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.pot_flower", "potted flowers", "scoreboard objectives add K.pot_flower minecraft.custom:minecraft.pot_flower", context.getSource(), Stats.POT_FLOWER, persistent))).
                then(literal("bell_ring").
                        executes(context -> scoreboard.startThreadedCommandScoreboard("K.bell_ring", "bell ring", "scoreboard objectives add K.bell_ring minecraft.custom:minecraft.bell_ring", context.getSource(), Stats.BELL_RING, persistent))).
                then(literal("remove").
                        executes(context -> scoreboard.hideSidebar(context.getSource())));
    }
}
