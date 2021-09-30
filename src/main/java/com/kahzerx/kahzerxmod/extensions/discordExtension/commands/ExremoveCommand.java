package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public class ExremoveCommand extends GenericCommand {
    public ExremoveCommand(String prefix) {
        super("exremove", DiscordPermission.ADMIN_CHAT, prefix + "exremove <playerName>");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix) {
        System.out.println("exremove hit");
    }
}
