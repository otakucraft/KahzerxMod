package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public class BanCommand extends GenericCommand {
    public BanCommand(String prefix) {
        super("ban", DiscordPermission.ADMIN_CHAT, prefix + "ban <playerName>");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix) {
        System.out.println("ban hit");
    }
}
