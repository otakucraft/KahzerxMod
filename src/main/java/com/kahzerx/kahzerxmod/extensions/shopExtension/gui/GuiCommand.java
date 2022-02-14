package com.kahzerx.kahzerxmod.extensions.shopExtension.gui;

import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class GuiCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("K-GUI").
                requires(server -> extension.extensionSettings().isEnabled()).
                executes(context -> {
                    extension.openGUI(context.getSource().getPlayer());
                    return 1;
                }));
    }
}
