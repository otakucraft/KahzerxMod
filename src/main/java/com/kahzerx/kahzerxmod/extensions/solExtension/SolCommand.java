package com.kahzerx.kahzerxmod.extensions.solExtension;

import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;


public class SolCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SolExtension sol) {
        dispatcher.register(literal("sol").
                requires(enabled -> sol.extensionSettings().isEnabled()).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    player.setOnFireFor(3);
                    context.getSource().sendFeedback(MarkEnum.SUN.appendMessage("Very hot indeed!"), false);
                    return 1;
                }));
    }
}
