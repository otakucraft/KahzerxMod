package com.kahzerx.kahzerxmod.extensions.discordExtension;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public interface DiscordCommandsExtension {
    boolean processCommands(MessageReceivedEvent event, String message, MinecraftServer server);
}
