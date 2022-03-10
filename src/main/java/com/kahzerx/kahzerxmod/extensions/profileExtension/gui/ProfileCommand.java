package com.kahzerx.kahzerxmod.extensions.profileExtension.gui;

import com.kahzerx.kahzerxmod.extensions.profileExtension.ProfileExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ProfileCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ProfileExtension extension) {
        dispatcher.register(literal("kProfile").
                requires(server -> extension.extensionSettings().isEnabled()).
                executes(context -> {
                    extension.openGUI(context.getSource().getPlayer());
                    return 1;
                }));
    }
}
