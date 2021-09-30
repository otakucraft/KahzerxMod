package com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class DiscordSettings extends ExtensionSettings {
    private String token;
    private boolean crossServerChat;
    private String prefix;
    private boolean running;
    private long chatChannelID;
    private List<Long> allowedChats;
    public DiscordSettings(String name, boolean enabled, String description, String token, boolean crossServerChat, String prefix, boolean running, long chatChannelID, List<Long> allowedChats) {
        super(name, enabled, description);
        this.token = token;
        this.crossServerChat = crossServerChat;
        this.prefix = prefix;
        this.running = running;
        this.chatChannelID = chatChannelID;
        this.allowedChats = allowedChats;
    }

    public String getToken() {
        return token;
    }

    public boolean isCrossServerChat() {
        return crossServerChat;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isRunning() {
        return running;
    }

    public long getChatChannelID() {
        return chatChannelID;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<Long> getAllowedChats() {
        return allowedChats;
    }
}
