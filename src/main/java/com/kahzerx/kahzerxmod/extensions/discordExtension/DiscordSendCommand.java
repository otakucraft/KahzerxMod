package com.kahzerx.kahzerxmod.extensions.discordExtension;

import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DiscordSendCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, DiscordExtension discordExtension) {
        dispatcher.register(literal("discordSend").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2) && discordExtension.extensionSettings().isEnabled()).
                then(argument("message", MessageArgumentType.message()).
                        executes(context -> {
                            context.getSource().getServer().getPlayerManager().broadcast(
                                    MessageArgumentType.getMessage(context, "message"),
                                    MessageType.CHAT
                            );
                            DiscordListener.sendDiscordMessage(MessageArgumentType.getMessage(context, "message").getString());
                            return 1;
                        })));
    }
}
