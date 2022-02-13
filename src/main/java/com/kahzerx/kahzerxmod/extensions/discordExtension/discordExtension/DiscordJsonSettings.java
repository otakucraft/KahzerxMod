package com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension;

import java.util.List;

public class DiscordJsonSettings {
    private List<DiscordSettings> settings;

    public DiscordJsonSettings(List<DiscordSettings> settings) {
        this.settings = settings;
    }

    public List<DiscordSettings> getSettings() {
        return settings;
    }
}
