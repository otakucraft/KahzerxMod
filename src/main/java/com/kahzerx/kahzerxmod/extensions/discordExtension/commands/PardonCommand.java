package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public class PardonCommand extends GenericCommand {
    public PardonCommand(String prefix) {
        super("pardon", DiscordPermission.ADMIN_CHAT, prefix + "pardon <playerName>");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix) {
        System.out.println("pardon hit");
    }
}
