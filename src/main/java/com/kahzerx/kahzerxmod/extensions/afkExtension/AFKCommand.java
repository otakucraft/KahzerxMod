package com.kahzerx.kahzerxmod.extensions.afkExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class AFKCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, AFKExtension afk) {
        dispatcher.register(literal("afk").
                requires(extension -> afk.extensionSettings().isEnabled()).
                executes(context -> afk.onAFK(context.getSource())));
    }
}
