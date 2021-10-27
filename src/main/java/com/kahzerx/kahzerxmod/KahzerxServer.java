package com.kahzerx.kahzerxmod;

import com.kahzerx.kahzerxmod.database.ServerDatabase;
import com.kahzerx.kahzerxmod.utils.FileUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.WorldSavePath;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class KahzerxServer {
    public static MinecraftServer minecraftServer;
    public static List<Extensions> extensions = new ArrayList<>();
    public static ServerDatabase db = new ServerDatabase();
    public static CommandDispatcher<ServerCommandSource> dispatcher;

    public static void onRunServer(MinecraftServer minecraftServer) {
        KahzerxServer.minecraftServer = minecraftServer;
        ExtensionManager.manageExtensions(FileUtils.loadConfig(minecraftServer.getSavePath(WorldSavePath.ROOT).toString()));

        extensions.forEach(e -> e.onServerRun(minecraftServer));

        ExtensionManager.saveSettings();

        extensions.forEach(e -> e.onRegisterCommands(dispatcher));

        LiteralArgumentBuilder<ServerCommandSource> settingsCommand = literal("KSettings").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (Extensions ex : extensions) {
            LiteralArgumentBuilder<ServerCommandSource> extensionSubCommand = literal(ex.extensionSettings().getName());
            extensionSubCommand.
                    then(literal("enable").
                            executes(context -> {
                                if (ex.extensionSettings().isEnabled()) {
                                    context.getSource().sendFeedback(new LiteralText("Already enabled!"), false);
                                    return 1;
                                }
                                ex.extensionSettings().setEnabled(true);
                                ex.onExtensionEnabled();
                                ExtensionManager.saveSettings();
                                context.getSource().sendFeedback(new LiteralText("Extension enabled!"), false);
                                return 1;
                            })).
                    then(literal("disable").
                            executes(context -> {
                                if (!ex.extensionSettings().isEnabled()) {
                                    context.getSource().sendFeedback(new LiteralText("Already disabled!"), false);
                                    return 1;
                                }
                                ex.extensionSettings().setEnabled(false);
                                ex.onExtensionDisabled();
                                ExtensionManager.saveSettings();
                                context.getSource().sendFeedback(new LiteralText("Extension disabled!"), false);
                                return 1;
                            })).
                    executes(context -> {
                        context.getSource().sendFeedback(
                                new LiteralText(String.format(
                                        "[%s] > %s\n%s",
                                        ex.extensionSettings().getName(),
                                        ex.extensionSettings().isEnabled(),
                                        ex.extensionSettings().getDescription()
                                )), false);
                        return 1;
                    });
            ex.settingsCommand(extensionSubCommand);  // Otros ajustes por si fueran necesarios para las extensiones más complejas.
            settingsCommand.then(extensionSubCommand);
        }
        dispatcher.register(settingsCommand);
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
        extensions.forEach(Extensions::onServerStop);
        db.close();
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
