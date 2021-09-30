package com.kahzerx.kahzerxmod.extensions.homeExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, HomeExtension home) {
        dispatcher.register(literal("setHome").
                executes(context -> home.saveHome(context.getSource())));
    }
}
