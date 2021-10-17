package com.kahzerx.kahzerxmod.extensions.permsExtension;

import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PermsCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, PermsExtension perms) {
        dispatcher.register(literal("kPerms").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2) && perms.extensionSettings().isEnabled()).
                then(literal("give").
                        then(argument("player", word()).
                                suggests((c, b) -> suggestMatching(PlayerUtils.getPlayers(c.getSource()), b)).
                                then(argument("level", IntegerArgumentType.integer(1, 2)).
                                        executes(context -> perms.updatePerms(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "player"),
                                                IntegerArgumentType.getInteger(context, "level")))))));
    }
}
