package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ExaddCommand extends GenericCommand {
    public ExaddCommand(String prefix) {
        super("exadd", DiscordPermission.ADMIN_CHAT, prefix + "exadd <playerName>");
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
        Whitelist whitelist = server.getPlayerManager().getWhitelist();
        if (whitelist.isAllowed(profile.get())) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**" + playerName + " ya estaba en whitelist.**"}, serverPrefix, true, Color.YELLOW, true);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if (extension.alreadyAddedBySomeone(profile.get().getId().toString())) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**Ya está añadido a la whitelist.**"}, serverPrefix, true, Color.RED, true);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        WhitelistEntry whitelistEntry = new WhitelistEntry(profile.get());
        if (extension.isPlayerBanned(profile.get().getId().toString())) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**Parece que intentas añadir a alguien ya baneado.**"}, serverPrefix, true, Color.RED, true);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        extension.addPlayer(69420L, profile.get().getId().toString(), profile.get().getName());
        whitelist.add(whitelistEntry);
        EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**" + profile.get().getName() + " añadido :D**"}, serverPrefix, true, Color.GREEN, true);
        assert embed != null;
        event.getChannel().sendMessageEmbeds(embed.build()).queue();

        Guild guild = event.getGuild();
        Role role = guild.getRoleById(extension.extensionSettings().getDiscordRole());
        Member member = event.getMember();
        assert role != null;
        assert member != null;
        guild.addRoleToMember(member, role).queue();
    }
}
