package com.kahzerx.kahzerxmod;

import com.kahzerx.kahzerxmod.config.KSettings;
import com.kahzerx.kahzerxmod.database.ServerDatabase;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.utils.FileUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.util.ArrayList;
import java.util.List;

public class KahzerxServer {
    public static MinecraftServer minecraftServer;
    public static List<Extensions> extensions = new ArrayList<>();
    public static ServerDatabase db = new ServerDatabase();
    public static CommandDispatcher<ServerCommandSource> dispatcher;

    public static void onRunServer(MinecraftServer minecraftServer) {
        KahzerxServer.minecraftServer = minecraftServer;
        ExtensionManager.manageExtensions(FileUtils.loadConfig(minecraftServer.getSavePath(WorldSavePath.ROOT).toString()));

        extensions.forEach(e -> e.onServerRun(minecraftServer));

        List<ExtensionSettings> settingsArray = new ArrayList<>();
        for (Extensions ex : extensions) {
            settingsArray.add(ex.extensionSettings());
        }
        KSettings settings = new KSettings(settingsArray);
        FileUtils.createConfig(minecraftServer.getSavePath(WorldSavePath.ROOT).toString(), settings);

        extensions.forEach(e -> e.onRegisterCommands(dispatcher));
    }

    public static void onCreateDatabase() {
        db = new ServerDatabase();
        db.initializeConnection(minecraftServer.getSavePath(WorldSavePath.ROOT).toString());
        db.createPlayerTable();
        extensions.forEach(e -> e.onCreateDatabase(db.getConnection()));
    }

    public static void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        KahzerxServer.dispatcher = dispatcher;
    }

    public static void onStopServer() {
        db.close();
        extensions.forEach(Extensions::onServerStop);
    }

    public static void onAutoSave() {
        extensions.forEach(Extensions::onAutoSave);
    }

    public static void onPlayerJoined(ServerPlayerEntity player) {
        db.getQuery().insertPlayerUUID(player.getUuidAsString(), player.getName().getString());
        extensions.forEach(e -> e.onPlayerJoined(player));
    }

    public static void onPlayerLeft(ServerPlayerEntity player) {
        extensions.forEach(e -> e.onPlayerLeft(player));
    }

    public static void onPlayerDied(ServerPlayerEntity player) {
        extensions.forEach(e -> e.onPlayerDied(player));
    }

    public static void onChatMessage(ServerPlayerEntity player, String chatMessage) {
        extensions.forEach(e -> e.onChatMessage(player, chatMessage));
    }

    public static void onAdvancement(String advancement) {
        extensions.forEach(e -> e.onAdvancement(advancement));
    }
}
