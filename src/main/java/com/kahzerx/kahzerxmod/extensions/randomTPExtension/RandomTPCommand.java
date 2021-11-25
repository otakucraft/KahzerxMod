package com.kahzerx.kahzerxmod.extensions.randomTPExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class RandomTPCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, RandomTPExtension rTP) {
        dispatcher.register(literal("randomTP").
                requires(server -> rTP.extensionSettings().isEnabled()).
                executes(context -> rTP.tpAndSpawnPoint(context.getSource())));
    }
}
