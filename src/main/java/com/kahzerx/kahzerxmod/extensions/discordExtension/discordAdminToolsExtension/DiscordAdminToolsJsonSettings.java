package com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension;

import java.util.List;

public class DiscordAdminToolsJsonSettings {
    private List<DiscordAdminToolsSettings> settings;

    public DiscordAdminToolsJsonSettings(List<DiscordAdminToolsSettings> settings) {
        this.settings = settings;
    }

    public List<DiscordAdminToolsSettings> getSettings() {
        return settings;
    }
}
