package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListCommand extends GenericCommand {
    public ListCommand(String prefix) {
        super("list", DiscordPermission.WHITELIST_CHAT, prefix + "list");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix) {
        String[] names = server.getPlayerManager().getWhitelistedNames();
        EmbedBuilder embed;
        if (names.length == 0) {
            embed = DiscordChatUtils.generateEmbed(new String[]{"La whitelist está vacía :("}, serverPrefix, true, Color.RED, true);
        } else {
            int maxNames = 60;
            names = String.join(",", names).toLowerCase().split(",");
            Arrays.sort(names);
            if (names.length > maxNames) {
                List<String> playerList = new ArrayList<>();
                for (String n : names) {
                    if (playerList.size() == maxNames) {
                        embed = DiscordChatUtils.generateEmbed(playerList.toArray(new String[0]), serverPrefix, true, Color.GREEN, false);
                        assert embed != null;
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        playerList.clear();
                    }
                    playerList.add(n);
                }
                embed = DiscordChatUtils.generateEmbed(playerList.toArray(new String[0]), serverPrefix, true, Color.GREEN, false);
                assert embed != null;
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            } else {
                embed = DiscordChatUtils.generateEmbed(names, serverPrefix, true, Color.GREEN, false);
            }
        }
        assert embed != null;
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
