package com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class DiscordAdminToolsSettings extends ExtensionSettings {
    private List<Long> adminChats;
    private boolean shouldFeedback;
    public DiscordAdminToolsSettings(String name, boolean enabled, String description, List<Long> adminChats, boolean shouldFeedback) {
        super(name, enabled, description);
        this.adminChats = adminChats;
        this.shouldFeedback = shouldFeedback;
    }

    public boolean isShouldFeedback() {
        return shouldFeedback;
    }

    public void setShouldFeedback(boolean shouldFeedback) {
        this.shouldFeedback = shouldFeedback;
    }

    public List<Long> getAdminChats() {
        return adminChats;
    }

    public void addAdminChatID(long chatID) {
        this.adminChats.add(chatID);
    }

    public void removeAdminChatID(long chatID) {
        this.adminChats.remove(chatID);
    }
}
