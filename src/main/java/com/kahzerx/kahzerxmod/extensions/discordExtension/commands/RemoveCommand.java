package com.kahzerx.kahzerxmod.extensions.discordExtension.commands;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordPermission;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.utils.DiscordChatUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RemoveCommand extends GenericCommand {
    public RemoveCommand(String prefix) {
        super("remove", DiscordPermission.WHITELIST_CHAT, prefix + "remove <playerName>");
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server, String serverPrefix, DiscordWhitelistExtension extension) {
        boolean feedback = extension.getDiscordExtension().extensionSettings().isShouldFeedback();
        long id = event.getAuthor().getIdLong();
        if (extension.isDiscordBanned(id)) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**Looks like you are banned...**"}, serverPrefix, true, Color.RED, true, feedback);
            assert embed != null;
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        String[] req = event.getMessage().getContentRaw().split(" ");
        String playerName = req[1];
        if (req.length != 2) {
            event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
            this.sendHelpCommand(serverPrefix, event.getChannel(), feedback);
            return;
        }
        Optional<GameProfile> profile = server.getUserCache().findByName(playerName);
        if (profile.isEmpty()) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**Not premium.**"}, serverPrefix, true, Color.RED, true, feedback);
            if (embed != null) {
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
            return;
        }
        Whitelist whitelist = server.getPlayerManager().getWhitelist();
        if (!whitelist.isAllowed(profile.get())) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**" + playerName + " is not whitelisted.**"}, serverPrefix, true, Color.YELLOW, true, feedback);
            if (embed != null) {
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
            return;
        }
        if (!extension.canRemove(id, profile.get().getId().toString())) {
            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**You can't remove " + profile.get().getName() + ".**"}, serverPrefix, true, Color.RED, true, feedback);
            if (embed != null) {
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }
            return;
        }
        WhitelistEntry whitelistEntry = new WhitelistEntry(profile.get());
        extension.deletePlayer(id, profile.get().getId().toString());
        whitelist.remove(whitelistEntry);
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(profile.get().getId());
        if (player != null) {
            player.networkHandler.disconnect(new LiteralText("Byee~"));
        }

        if (extension.getWhitelistedPlayers(id).isEmpty()) {
            Guild guild = event.getGuild();
            Role role = guild.getRoleById(extension.extensionSettings().getDiscordRole());
            Member member = event.getMember();
            if (role != null && member != null) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }

        EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{"**" + profile.get().getName() + " removed D:**"}, serverPrefix, true, Color.GREEN, true, feedback);
        if (embed != null) {
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
