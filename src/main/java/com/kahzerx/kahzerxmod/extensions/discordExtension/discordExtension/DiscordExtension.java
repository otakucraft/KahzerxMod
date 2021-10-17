package com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class DiscordExtension extends GenericExtension implements Extensions {
    public DiscordExtension(DiscordSettings settings) {
        super(settings);
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (!extensionSettings().isEnabled()) {
            return;
        }
        DiscordListener.start(minecraftServer, extensionSettings().getToken(), String.valueOf(extensionSettings().getChatChannelID()), this);
        DiscordListener.sendDiscordMessage("\\o/");
    }

    @Override
    public void onServerStop() {
        if (extensionSettings().isRunning()) {
            DiscordListener.stop();
        }
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        DiscordListener.sendDiscordMessage(":arrow_right: **" + player.getName().getString().replace("_", "\\_") + " joined the game!**");
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        DiscordListener.sendDiscordMessage(":arrow_left: **" + player.getName().getString().replace("_", "\\_") + " left the game!**");
    }

    @Override
    public void onPlayerDied(ServerPlayerEntity player) {
        DiscordListener.sendDiscordMessage(":skull_crossbones: **" + player.getDamageTracker().getDeathMessage().getString().replace("_", "\\_") + "**");
    }

    @Override
    public void onChatMessage(ServerPlayerEntity player, String chatMessage) {
        if (chatMessage.startsWith("/me ") || !chatMessage.startsWith("/")) {
            DiscordListener.sendDiscordMessage("`<" + player.getName().getString() + ">` " + chatMessage);
        }
    }

    @Override
    public void onAdvancement(String advancement) {
        DiscordListener.sendDiscordMessage(":confetti_ball: **" + advancement + "**");
    }

    @Override
    public DiscordSettings extensionSettings() {
        return (DiscordSettings) this.getSettings();
    }

    @Override
    public void onExtensionEnabled() {

    }

    @Override
    public void onExtensionDisabled() {

    }
}
