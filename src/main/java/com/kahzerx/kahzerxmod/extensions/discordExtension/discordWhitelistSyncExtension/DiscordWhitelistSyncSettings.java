package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class DiscordWhitelistSyncSettings extends ExtensionSettings {
    private long notifyChannelID;
    private List<Long> validRoles;
    private long groupID;
    private boolean aggressive;
    public DiscordWhitelistSyncSettings(String name, boolean enabled, String description, long notifyChannelID, List<Long> validRoles, long groupID, boolean aggressive) {
        super(name, enabled, description);
        this.notifyChannelID = notifyChannelID;
        this.validRoles = validRoles;
        this.groupID = groupID;
        this.aggressive = aggressive;
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

    public boolean isAggressive() {
        return aggressive;
    }

    public void setNotifyChannelID(long notifyChannelID) {
        this.notifyChannelID = notifyChannelID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    public void addValidRoleID(long chatID) {
        this.validRoles.add(chatID);
    }

    public void removeValidRoleID(long chatID) {
        this.validRoles.remove(chatID);
    }
}
