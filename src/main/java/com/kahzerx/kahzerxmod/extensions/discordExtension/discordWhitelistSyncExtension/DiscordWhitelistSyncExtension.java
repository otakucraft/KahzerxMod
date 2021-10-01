package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import net.minecraft.server.MinecraftServer;

import java.sql.Connection;

public class DiscordWhitelistSyncExtension extends GenericExtension implements Extensions {
    private final DiscordExtension discordExtension;
    private final DiscordWhitelistExtension discordWhitelistExtension;
    private DiscordWhitelistSyncThread thread = null;
    private MinecraftServer server;
    public DiscordWhitelistSyncExtension(ExtensionSettings settings, DiscordExtension discordExtension, DiscordWhitelistExtension discordWhitelistExtension) {
        super(settings);
        this.discordExtension = discordExtension;
        this.discordWhitelistExtension = discordWhitelistExtension;
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return;
        }
        if (!discordWhitelistExtension.getSettings().isEnabled()) {
            return;
        }
        this.server = minecraftServer;
    }

    @Override
    public void onAutoSave() {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return;
        }
        if (!discordWhitelistExtension.getSettings().isEnabled()) {
            return;
        }
        this.thread = new DiscordWhitelistSyncThread("WHITELIST_SYNC", this.server, this.discordWhitelistExtension, this);
        this.thread.start();
    }

    @Override
    public void onServerStop() {
        if (!this.getSettings().isEnabled()) {
            return;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return;
        }
        if (!discordWhitelistExtension.getSettings().isEnabled()) {
            return;
        }
        if (this.thread == null) {
            return;
        }
        if (this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DiscordWhitelistSyncSettings extensionSettings() {
        return (DiscordWhitelistSyncSettings) this.getSettings();
    }

    @Override
    public void onExtensionEnabled() {

    }

    @Override
    public void onExtensionDisabled() {

    }
}
