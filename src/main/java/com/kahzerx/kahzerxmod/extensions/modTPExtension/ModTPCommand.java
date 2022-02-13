package com.kahzerx.kahzerxmod.extensions.modTPExtension;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModTPCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ModTPExtension modTPExtension) {
        dispatcher.register(literal("modTP").
                requires(serverCommandSource -> {
                    try {
                        if (modTPExtension.extensionSettings().isEnabled() && modTPExtension.permsExtension.extensionSettings().isEnabled()) {
                            return modTPExtension.permsExtension.getDBPlayerPerms(serverCommandSource.getPlayer().getUuidAsString()).getId() >= PermsLevels.MOD.getId();
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).
                then(argument("player", word()).
                        suggests((c, b) -> suggestMatching(PlayerUtils.getPlayers(c.getSource()), b)).
                        executes(context -> modTPExtension.tp(context.getSource(), getString(context, "player")))));
    }
}
