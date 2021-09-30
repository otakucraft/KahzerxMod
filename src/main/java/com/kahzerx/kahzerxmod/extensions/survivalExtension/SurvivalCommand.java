package com.kahzerx.kahzerxmod.extensions.survivalExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class SurvivalCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SurvivalExtension survival) {
        dispatcher.register(literal("s").
                executes(context -> survival.setSurvivalMode(context.getSource())));
    }
}
