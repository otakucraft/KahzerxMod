package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.utils.DiscordChatUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class DiscordWhitelistSyncThread extends TimerTask {
    private static final Logger LOGGER = LogManager.getLogger();

    private final DiscordWhitelistExtension discordWhitelistExtension;
    private final DiscordExtension discordExtension;
    private final DiscordWhitelistSyncExtension discordWhitelistSyncExtension;
    private final MinecraftServer server;
    public DiscordWhitelistSyncThread(MinecraftServer server, DiscordExtension discordExtension, DiscordWhitelistExtension discordWhitelistExtension, DiscordWhitelistSyncExtension discordWhitelistSyncExtension) {
        this.discordExtension = discordExtension;
        this.discordWhitelistExtension = discordWhitelistExtension;
        this.discordWhitelistSyncExtension = discordWhitelistSyncExtension;
        this.server = server;
    }

    @Override
    public void run() {
        if (DiscordListener.jda == null) {
            return;
        }
        if (!this.discordExtension.extensionSettings().isEnabled() || !this.discordWhitelistExtension.extensionSettings().isEnabled() || !this.discordWhitelistSyncExtension.extensionSettings().isEnabled()) {
            return;
        }
        try {
            LOGGER.info("STARTING WHITELIST SYNC.");
            database2WhitelistSync();
            if (discordWhitelistSyncExtension.extensionSettings().isAggressive()) {
                server.getPlayerManager().reloadWhitelist();
                whitelist2Database();
                database2Whitelist();
            }
            LOGGER.info("WHITELIST SYNC FINISHED.");
        } catch (NullPointerException ignored) {}
    }

    private void database2WhitelistSync() {
        ArrayList<Long> discordIDsPre = discordWhitelistExtension.getDiscordIDs();
        ArrayList<Long> finalDiscordIDs = discordWhitelistExtension.getDiscordIDs();
        ArrayList<Long> discordIDs = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();
        List<Long> validRoles = discordWhitelistSyncExtension.extensionSettings().getValidRoles();
        if (validRoles.isEmpty()) {
            return;
        }
        Guild guild = DiscordListener.jda.getGuildById(discordWhitelistSyncExtension.extensionSettings().getGroupID());
        if (guild == null) {
            return;
        }
        try {
            for (long id : discordIDsPre) {
                if (discordWhitelistExtension.isDiscordBanned(id) || id == 69420L) {
                    finalDiscordIDs.remove(id);
                    continue;
                }
                discordIDs.add(id);
                if (discordIDs.size() > 80) {
                    memberList.addAll(guild.retrieveMembersByIds(discordIDs).get());
                    discordIDs.clear();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (discordIDs.size() > 0) {
                memberList.addAll(guild.retrieveMembersByIds(discordIDs).get());
                discordIDs.clear();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (ErrorResponseException responseException) {
            if (responseException.isServerError() || (400 < responseException.getErrorCode() && responseException.getErrorCode() < 500)) {
                responseException.printStackTrace();
                return;
            }
            LOGGER.error("Unable to find Member, removing from whitelist...");
            return;
        }

        for (Member member : memberList) {
            List<Role> roles = member.getRoles();
            boolean hasValidRole = false;
            for (Role role : roles) {
                if (validRoles.contains(role.getIdLong())) {
                    hasValidRole = true;
                    break;
                }
            }
            if (hasValidRole) {
                finalDiscordIDs.remove(member.getIdLong());
                continue;
            }
            Role role = guild.getRoleById(discordWhitelistExtension.extensionSettings().getDiscordRole());
            if (role != null) {
                try {
                    guild.removeRoleFromMember(member, role).queue();
                } catch (HierarchyException exception) {
                    exception.printStackTrace();
                }
            }
            onSyncAction(discordWhitelistExtension.getWhitelistedPlayers(member.getIdLong()), member.getIdLong(), guild);
        }

        for (long id : finalDiscordIDs) {
            onSyncAction(discordWhitelistExtension.getWhitelistedPlayers(id), id, guild);
        }
    }

    private void onSyncAction(ArrayList<String> whitelistedPlayers, long discordID, Guild guild) {
        for (String whitelistedPlayerUUID : whitelistedPlayers) {
            Optional<GameProfile> p = server.getUserCache().getByUuid(UUID.fromString(whitelistedPlayerUUID));
            if (p.isEmpty()) {
                continue;
            }
            discordWhitelistExtension.tryVanillaWhitelistRemove(server.getPlayerManager().getWhitelist(), p.get(), server);
            discordWhitelistExtension.deletePlayer(discordID, whitelistedPlayerUUID);

            EmbedBuilder embed = DiscordChatUtils.generateEmbed(new String[]{String.format("**F %s**", p.get().getName())}, "", true, Color.RED, true, discordWhitelistExtension.getDiscordExtension().extensionSettings().isShouldFeedback());
            if (embed != null) {
                TextChannel channel = guild.getTextChannelById(discordWhitelistSyncExtension.extensionSettings().getNotifyChannelID());
                assert channel != null;
                channel.sendMessageEmbeds(embed.build()).queue();
            }
        }
    }

    private void whitelist2Database() {
        List<String> whitelistedUUIDs = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(server.getPlayerManager().getWhitelist().getFile()));
            JSONArray jsonArray = (JSONArray) obj;
            for (JSONObject jsonObject : (Iterable<JSONObject>) jsonArray) {
                whitelistedUUIDs.add((String) jsonObject.get("uuid"));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        Whitelist whitelist = server.getPlayerManager().getWhitelist();
        for (String uuid : whitelistedUUIDs) {
            Optional<GameProfile> profile = server.getUserCache().getByUuid(UUID.fromString(uuid));
            if (profile.isEmpty()) {
                continue;
            }
            GameProfile p = profile.get();
            if (discordWhitelistExtension.alreadyAddedBySomeone(p.getId())) {
                continue;
            }
            discordWhitelistExtension.tryVanillaWhitelistRemove(whitelist, p, server);
        }
    }

    private void database2Whitelist() {
        ArrayList<Long> discordIDs = discordWhitelistExtension.getDiscordIDs();
        for (long discordID : discordIDs) {
            ArrayList<String> whitelistedUUID = discordWhitelistExtension.getWhitelistedPlayers(discordID);
            for (String playerUUID : whitelistedUUID) {
                Optional<GameProfile> profile = server.getUserCache().getByUuid(UUID.fromString(playerUUID));
                if (profile.isEmpty()) {
                    discordWhitelistExtension.deletePlayer(discordID, playerUUID);
                    continue;
                }
                Whitelist whitelist = server.getPlayerManager().getWhitelist();
                if (whitelist.isAllowed(profile.get())) {
                    continue;
                }
                WhitelistEntry entry = new WhitelistEntry(profile.get());
                whitelist.add(entry);
            }
        }
    }
}
