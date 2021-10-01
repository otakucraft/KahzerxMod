package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class DiscordWhitelistSyncSettings extends ExtensionSettings {
    private long notifyChannelID;
    private List<Long> validRoles;
    private long groupID;
    public DiscordWhitelistSyncSettings(String name, boolean enabled, String description, long notifyChannelID, List<Long> validRoles, long groupID) {
        super(name, enabled, description);
        this.notifyChannelID = notifyChannelID;
        this.validRoles = validRoles;
        this.groupID = groupID;
    }

    public List<Long> getValidRoles() {
        return validRoles;
    }

    public long getNotifyChannelID() {
        return notifyChannelID;
    }

    public long getGroupID() {
        return groupID;
    }
}
