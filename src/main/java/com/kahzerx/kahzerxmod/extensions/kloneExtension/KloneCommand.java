package com.kahzerx.kahzerxmod.extensions.kloneExtension;

import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class KloneCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, KloneExtension klone) {
        dispatcher.register(literal("klone").
                requires(isEnabled -> klone.extensionSettings().isEnabled()).
                executes(context -> {
                    ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
                    KlonePlayerEntity.createKlone(context.getSource().getServer(), sourcePlayer);
                    return 1;
                }));
    }
}
