package com.kahzerx.kahzerxmod.extensions.totopoExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class TotopoCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, TotopoExtension totopo) {
        dispatcher.register(literal("totopo").
                requires(server -> totopo.extensionSettings().isEnabled()));
    }
}
