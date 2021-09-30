package com.kahzerx.kahzerxmod.extensions.hereExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, HereExtension here) {
        dispatcher.register(literal("here").
                executes(context -> here.sendLocation(context.getSource())));
    }
}
