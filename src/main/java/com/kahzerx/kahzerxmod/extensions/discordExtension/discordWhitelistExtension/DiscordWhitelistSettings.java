package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class DiscordWhitelistSettings extends ExtensionSettings {
    private List<Long> whitelistChats;
    private long discordRoleID;
    private int nPlayers;
    public DiscordWhitelistSettings(String name, boolean enabled, String description, List<Long> whitelistChats, long discordRoleID, int nPlayers) {
        super(name, enabled, description);
        this.whitelistChats = whitelistChats;
        this.discordRoleID = discordRoleID;
        this.nPlayers = nPlayers;
    }

    public List<Long> getWhitelistChats() {
        return whitelistChats;
    }

    public long getDiscordRole() {
        return discordRoleID;
    }

    public int getNPlayers() {
        return nPlayers;
    }

    public void setDiscordRoleID(long discordRoleID) {
        this.discordRoleID = discordRoleID;
    }

    public void setNPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    public void addWhitelistChatID(long chatID) {
        this.whitelistChats.add(chatID);
    }

    public void removeWhitelistChatID(long chatID) {
        this.whitelistChats.remove(chatID);
    }
}
