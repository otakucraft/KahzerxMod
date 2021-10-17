package com.kahzerx.kahzerxmod.extensions.homeExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, HomeExtension home) {
        dispatcher.register(literal("home").
                requires(server -> home.extensionSettings().isEnabled()).
                executes(context -> home.tpHome(context.getSource())));
    }
}
