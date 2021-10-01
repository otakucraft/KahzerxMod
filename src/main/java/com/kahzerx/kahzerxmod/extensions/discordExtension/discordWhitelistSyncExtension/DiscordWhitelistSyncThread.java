package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DiscordWhitelistSyncThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final DiscordWhitelistExtension discordWhitelistExtension;
    private final DiscordWhitelistSyncExtension discordWhitelistSyncExtension;
    private final MinecraftServer server;
    public DiscordWhitelistSyncThread(String name, MinecraftServer server, DiscordWhitelistExtension discordWhitelistExtension, DiscordWhitelistSyncExtension discordWhitelistSyncExtension) {
        super(name);
        this.discordWhitelistExtension = discordWhitelistExtension;
        this.discordWhitelistSyncExtension = discordWhitelistSyncExtension;
        this.server = server;
    }

    @Override
    public void run() {
        if (DiscordListener.jda == null) {
            return;
        }
        ArrayList<Long> discordIDs = discordWhitelistExtension.getDiscordIDs();
        List<Long> validRoles = discordWhitelistSyncExtension.extensionSettings().getValidRoles();
        if (validRoles.isEmpty()) {
            return;
        }
        Guild guild = DiscordListener.jda.getGuildById(discordWhitelistSyncExtension.extensionSettings().getGroupID());
        if (guild == null) {
            return;
        }
        LOGGER.info("STARTING WHITELIST SYNC.");
        for (long discordID : discordIDs) {
            if (discordWhitelistExtension.isDiscordBanned(discordID)) {
                continue;
            }
            if (discordID == 69420L) {
                continue;
            }
            Member member = guild.getMemberById(discordID);
            if (member == null) {
                onSyncAction(discordWhitelistExtension.getWhitelistedPlayers(discordID), discordID, guild);
                continue;
            }
            List<Role> roles = member.getRoles();
            boolean hasValidRole = false;
            for (Role role : roles) {
                if (validRoles.contains(role.getIdLong())) {
                    hasValidRole = true;
                    break;
                }
            }
            if (hasValidRole) {
                continue;
            }
            Role role = guild.getRoleById(discordWhitelistExtension.extensionSettings().getDiscordRole());
            if (role != null) {
                guild.removeRoleFromMember(member, role).queue();
            }
            onSyncAction(discordWhitelistExtension.getWhitelistedPlayers(discordID), discordID, guild);
        }
        LOGGER.info("WHITELIST SYNC FINISHED.");
    }

    private void onSyncAction(ArrayList<String> whitelistedPlayers, long discordID, Guild guild) {
        for (String whitelistedPlayerUUID : whitelistedPlayers) {
            Optional<GameProfile> p = server.getUserCache().getByUuid(UUID.fromString(whitelistedPlayerUUID));
            if (p.isEmpty()) {
                continue;
            }
            discordWhitelistExtension.tryVanillaWhitelistRemove(server.getPlayerManager().getWhitelist(), p.get(), server);
            discordWhitelistExtension.deletePlayer(discordID, whitelistedPlayerUUID);

            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{String.format("**F %s**", p.get().getName())}, "", true, Color.RED, true);
            assert embed != null;
            TextChannel channel = guild.getTextChannelById(discordWhitelistSyncExtension.extensionSettings().getNotifyChannelID());
            assert channel != null;
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
