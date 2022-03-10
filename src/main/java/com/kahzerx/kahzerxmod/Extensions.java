package com.kahzerx.kahzerxmod;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.Connection;

public interface Extensions {
    default void onServerRun(MinecraftServer minecraftServer) {}
    default void onServerStarted(MinecraftServer minecraftServer) {}
    default void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {}
    default void onCreateDatabase(Connection conn) {}
    default void onCreateDatabase(String worldPath) {}
    default void onServerStop() {}
    default void onAutoSave() {}
    default void onPlayerJoined(ServerPlayerEntity player) {}
    default void onPlayerLeft(ServerPlayerEntity player) {}
    default void onPlayerDied(ServerPlayerEntity player) {}
    default void onChatMessage(ServerPlayerEntity player, String chatMessage) {}
    default void onAdvancement(String advancement) {}
    default void onTick(MinecraftServer server) {}
    default void onClick(ServerPlayerEntity player) {}

    default void onExtensionEnabled() {
        PlayerUtils.reloadCommands();
    }
    default void onExtensionDisabled() {
        PlayerUtils.reloadCommands();
    }
    default void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {}
    ExtensionSettings extensionSettings();
}
