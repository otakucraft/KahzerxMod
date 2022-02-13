package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import java.util.List;

public class DiscordWhitelistSyncJsonSettings {
    private List<DiscordWhitelistSyncSettings> settings;

    public DiscordWhitelistSyncJsonSettings(List<DiscordWhitelistSyncSettings> settings) {
        this.settings = settings;
    }

    public List<DiscordWhitelistSyncSettings> getSettings() {
        return settings;
    }
}
