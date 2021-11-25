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
    private boolean shouldFeedback;
    public DiscordSettings(String name, boolean enabled, String description, String token, boolean crossServerChat, String prefix, boolean running, long chatChannelID, List<Long> allowedChats, boolean shouldFeedback) {
        super(name, enabled, description);
        this.token = token;
        this.crossServerChat = crossServerChat;
        this.prefix = prefix;
        this.running = running;
        this.chatChannelID = chatChannelID;
        this.allowedChats = allowedChats;
        this.shouldFeedback = shouldFeedback;
    }

    public boolean isShouldFeedback() {
        return shouldFeedback;
    }

    public void setShouldFeedback(boolean shouldFeedback) {
        this.shouldFeedback = shouldFeedback;
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

    public void setToken(String token) {
        this.token = token;
    }

    public void setChatChannelID(long chatChannelID) {
        this.chatChannelID = chatChannelID;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setCrossServerChat(boolean crossServerChat) {
        this.crossServerChat = crossServerChat;
    }

    public void addAllowedChatID(long chatID) {
        this.allowedChats.add(chatID);
    }

    public void removeAllowedChatID(long chatID) {
        this.allowedChats.remove(chatID);
    }
}
