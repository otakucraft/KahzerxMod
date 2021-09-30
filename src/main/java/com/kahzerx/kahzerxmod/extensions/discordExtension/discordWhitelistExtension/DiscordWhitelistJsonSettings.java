package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension;

import java.util.List;

public class DiscordWhitelistJsonSettings {
    private List<DiscordWhitelistSettings> settings;

    public DiscordWhitelistJsonSettings(List<DiscordWhitelistSettings> settings) {
        this.settings = settings;
    }

    public List<DiscordWhitelistSettings> getSettings() {
        return settings;
    }
}
