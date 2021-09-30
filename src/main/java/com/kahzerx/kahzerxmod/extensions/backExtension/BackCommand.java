package com.kahzerx.kahzerxmod.extensions.backExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class BackCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, BackExtension back) {
        dispatcher.register(literal("back").
                executes(context -> back.tpBack(context.getSource())));
    }
}
