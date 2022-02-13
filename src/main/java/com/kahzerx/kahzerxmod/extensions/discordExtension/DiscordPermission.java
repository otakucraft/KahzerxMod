package com.kahzerx.kahzerxmod.extensions.discordExtension;

public enum DiscordPermission {
    ADMIN_CHAT(0),
    WHITELIST_CHAT(1),
    ALLOWED_CHAT(2);

    private final int id;

    DiscordPermission(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
