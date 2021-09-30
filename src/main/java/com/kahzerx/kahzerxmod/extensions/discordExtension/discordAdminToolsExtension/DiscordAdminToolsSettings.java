package com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class DiscordAdminToolsSettings extends ExtensionSettings {
    private List<Long> adminChats;
    public DiscordAdminToolsSettings(String name, boolean enabled, String description, List<Long> adminChats) {
        super(name, enabled, description);
        this.adminChats = adminChats;
    }

    public List<Long> getAdminChats() {
        return adminChats;
    }
}
