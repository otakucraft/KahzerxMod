package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PardonCommand extends GenericCommand {
    public PardonCommand(String prefix) {
        super("pardon", DiscordPermission.ADMIN_CHAT, prefix + "pardon <playerName>");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix, DiscordWhitelistExtension extension) {
        String[] req = event.getMessage().getContentRaw().split(" ");
        String playerName = req[1];
        if (req.length != 2) {
            event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
            this.sendHelpCommand(serverPrefix, event.getChannel());
            return;
        }
        Optional<GameProfile> profile = server.getUserCache().findByName(playerName);
        if (profile.isEmpty()) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**No es premium.**"}, serverPrefix, true, Color.RED, true);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if (!extension.alreadyAddedBySomeone(profile.get().getId().toString())) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**No ha sido baneado antes!**"}, serverPrefix, true, Color.RED, true);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        long discordID = extension.getDiscordID(profile.get().getId().toString());
        if (!extension.isPlayerBanned(profile.get().getId().toString())) {
            onPardonAction(extension, discordID, server);
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**No estaba baneado.**"}, serverPrefix, true, Color.YELLOW, true);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        extension.pardonDiscord(discordID);
        onPardonAction(extension, discordID, server);
        EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**Desbaneado! :D**"}, serverPrefix, true, Color.GREEN, true);
        assert embed != null;
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void onPardonAction(DiscordWhitelistExtension extension, long discordID, MinecraftServer server) {
        ArrayList<String> whitelistedPlayers = extension.getWhitelistedPlayers(discordID);
        for (String uuid : whitelistedPlayers) {
            Optional<GameProfile> p = server.getUserCache().getByUuid(UUID.fromString(uuid));
            if (p.isEmpty()) {
                continue;
            }
            extension.tryVanillaPardon(server.getPlayerManager().getUserBanList(), p.get());
            extension.deletePlayer(discordID, p.get().getId().toString());
        }
    }
}
