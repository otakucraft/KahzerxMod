package com.kahzerx.kahzerxmod.extensions.pitoExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class PitoCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, PitoExtension pito) {
        dispatcher.register(literal("pito").
                requires(server -> pito.extensionSettings().isEnabled()).
                executes(context -> pito.pitoResponse(context.getSource())));
    }
}
