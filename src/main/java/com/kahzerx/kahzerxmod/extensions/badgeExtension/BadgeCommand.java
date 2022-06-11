package com.kahzerx.kahzerxmod.extensions.badgeExtension;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BadgeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, BadgeExtension badge) {
        dispatcher.register(literal("badge").
                requires(serverCommandSource -> {
                    if (badge.extensionSettings().isEnabled() && badge.permsExtension.extensionSettings().isEnabled()) {
                        return badge.permsExtension.getDBPlayerPerms(serverCommandSource.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId();
                    }
                    return false;
                }).
                then(literal("create").
                        then(argument("badge", MessageArgumentType.message()).
                                executes(context -> {
                                    badge.insertBadge(context.getSource(), MessageArgumentType.getMessage(context, "badge").getString());
                                    return 1;
                                }))).
                then(literal("remove").
                        then(argument("badge", MessageArgumentType.message()).
                                suggests((context, builder) -> CommandSource.suggestMatching(badge.getAllBadges(), builder)).
                                executes(context -> {
                                    badge.removeBadge(context.getSource(), MessageArgumentType.getMessage(context, "badge").getString());
                                    return 1;
                                }))).
                then(literal("id").
                        then(argument("badge", MessageArgumentType.message()).
                                suggests((context, builder) -> CommandSource.suggestMatching(badge.getAllBadges(), builder)).
                                executes(context -> {
                                    badge.getID(context.getSource(), MessageArgumentType.getMessage(context, "badge").getString());
                                    return 1;
                                }))).
                then(literal("reload").
                        executes(context -> {
                            badge.reload();
                            return 1;
                        })).
                then(literal("modify").
                        then(argument("badge_id", IntegerArgumentType.integer()).
                                suggests((context, builder) -> CommandSource.suggestMatching(badge.getAllIDs(), builder)).
                                then(literal("color").
                                        then(argument("color", ColorArgumentType.color()).
                                                executes(context -> {
                                                    badge.modifyBadgeColor(context.getSource(), IntegerArgumentType.getInteger(context, "badge_id"), ColorArgumentType.getColor(context, "color"));
                                                    return 1;
                                                }))).
                                then(literal("description").
                                        then(argument("desc", MessageArgumentType.message()).
                                                executes(context -> {
                                                    badge.modifyBadgeDesc(context.getSource(), IntegerArgumentType.getInteger(context, "badge_id"), MessageArgumentType.getMessage(context, "desc").getString());
                                                    return 1;
                                                }))))).
                then(literal("add").
                        then(argument("player", StringArgumentType.word()).
                                suggests((context, builder) -> CommandSource.suggestMatching(badge.getAllPlayers(), builder)).
                                then(argument("badge", MessageArgumentType.message()).
                                        suggests((context, builder) -> CommandSource.suggestMatching(badge.getNotPlayerBadges(StringArgumentType.getString(context, "player")), builder)).
                                        executes(context -> {
                                            badge.addBadge(context.getSource(), MessageArgumentType.getMessage(context, "badge").getString(), StringArgumentType.getString(context, "player"));
                                            return 1;
                                        })))).
                then(literal("delete").
                        then(argument("player", StringArgumentType.word()).
                                suggests((context, builder) -> CommandSource.suggestMatching(badge.getAllPlayers(), builder)).
                                then(argument("badge", MessageArgumentType.message()).
                                        suggests((context, builder) -> CommandSource.suggestMatching(badge.getPlayerBadges(StringArgumentType.getString(context, "player")), builder)).
                                        executes(context -> {
                                            badge.deleteBadge(context.getSource(), MessageArgumentType.getMessage(context, "badge").getString(), StringArgumentType.getString(context, "player"));
                                            return 1;
                                        })))));
    }
}
