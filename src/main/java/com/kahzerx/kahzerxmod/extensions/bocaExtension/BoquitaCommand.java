package com.kahzerx.kahzerxmod.extensions.bocaExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.literal;

public class BoquitaCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, BocaExtension boca) {
        dispatcher.register(literal("boquita").
                requires(server -> boca.extensionSettings().isEnabled()).
                executes(context -> {
                    context.getSource().getServer().getPlayerManager().broadcastChatMessage(new LiteralText(
                            "§9❤§r §l§9BO§eQUI§9TA§r §9❤§r"
                    ), MessageType.CHAT, Util.NIL_UUID);
                    return 1;
                }));
    }
}
