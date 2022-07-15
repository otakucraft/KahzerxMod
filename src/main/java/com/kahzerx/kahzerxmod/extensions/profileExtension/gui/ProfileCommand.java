package com.kahzerx.kahzerxmod.extensions.profileExtension.gui;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.extensions.profileExtension.ProfileExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ProfileCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ProfileExtension extension) {
        dispatcher.register(literal("kProfile").
                requires(server -> extension.extensionSettings().isEnabled()).
                then(literal("bank").
                        requires(server -> server.hasPermissionLevel(2) || (extension.getShopExtension().getPermsExtension().extensionSettings().isEnabled() && extension.getShopExtension().getPermsExtension().getDBPlayerPerms(server.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId())).
                        executes(context -> {
                            extension.openGUI(context.getSource().getPlayer(), true);
                            return 1;
                        })).
                executes(context -> {
                    extension.openGUI(context.getSource().getPlayer(), false);
                    return 1;
                }));
    }
}
