package com.kahzerx.kahzerxmod.extensions.discordExtension;

import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DiscordSendCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, DiscordExtension discordExtension) {
        dispatcher.register(literal("discordSend").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2) && discordExtension.extensionSettings().isEnabled()).
                then(argument("message", MessageArgumentType.message()).
                        executes(context -> {
                            context.getSource().getServer().getPlayerManager().broadcastChatMessage(
                                    MessageArgumentType.getMessage(context, "message"),
                                    MessageType.CHAT,
                                    Util.NIL_UUID
                            );
                            DiscordListener.sendDiscordMessage(MessageArgumentType.getMessage(context, "message").asString());
                            return 1;
                        })));
    }
}
