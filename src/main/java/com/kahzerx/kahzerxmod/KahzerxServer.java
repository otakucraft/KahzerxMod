package com.kahzerx.kahzerxmod;

import com.kahzerx.kahzerxmod.database.ServerDatabase;
import com.kahzerx.kahzerxmod.profiler.*;
import com.kahzerx.kahzerxmod.utils.FileUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class KahzerxServer {
    public static MinecraftServer minecraftServer;
    public static List<Extensions> extensions = new ArrayList<>();
    public static List<AbstractProfiler> profilers = new ArrayList<>();
    public static ServerDatabase db = new ServerDatabase();
    public static CommandDispatcher<ServerCommandSource> dispatcher;

    public static void onRunServer(MinecraftServer minecraftServer) {
        profilers.add(new TPSProfiler());
        profilers.add(new RamProfiler());
        profilers.add(new MSPTProfiler());
        profilers.add(new PlayersProfiler());
        profilers.add(new BlockEntitiesProfiler());
        profilers.add(new EntityProfiler());
        profilers.add(new ChunkProfiler());

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
                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage(ex.extensionSettings().getName() + " extension already enabled"), false);
                                    return 1;
                                }
                                ex.extensionSettings().setEnabled(true);
                                ex.onExtensionEnabled();
                                ExtensionManager.saveSettings();
                                context.getSource().sendFeedback(MarkEnum.TICK.appendMessage(ex.extensionSettings().getName() + " extension enabled"), false);
                                return 1;
                            })).
                    then(literal("disable").
                            executes(context -> {
                                if (!ex.extensionSettings().isEnabled()) {
                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage(ex.extensionSettings().getName() + " extension already disabled"), false);
                                    return 1;
                                }
                                ex.extensionSettings().setEnabled(false);
                                ex.onExtensionDisabled();
                                ExtensionManager.saveSettings();
                                context.getSource().sendFeedback(MarkEnum.TICK.appendMessage(ex.extensionSettings().getName() + " extension disabled"), false);
                                return 1;
                            })).
                    executes(context -> {
                        context.getSource().sendFeedback(MarkEnum.INFO.appendMessage(String.format("[%s] > %s\n%s", ex.extensionSettings().getName(), ex.extensionSettings().isEnabled(), ex.extensionSettings().getDescription())), false);
                        return 1;
                    });
            ex.settingsCommand(extensionSubCommand);  // Otros ajustes por si fueran necesarios para las extensiones más complejas.
            settingsCommand.then(extensionSubCommand);
        }
        settingsCommand.executes(context -> {
            List<MutableText> extensionNames = new ArrayList<>();
            for (Extensions ex : extensions) {
                MutableText exData = new LiteralText(ex.extensionSettings().getName() + "\n").styled(
                        style -> style.
                                withBold(true).
                                withUnderline(false).
                                withColor(Formatting.WHITE).
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(ex.extensionSettings().getDescription()))));
                exData.append(new LiteralText("[True]").styled(
                        style -> style.
                                withBold(ex.extensionSettings().isEnabled()).
                                withUnderline(ex.extensionSettings().isEnabled()).
                                withColor(Formatting.GREEN).
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(String.format("Enable %s", ex.extensionSettings().getName())))).
                                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/KSettings %s enable", ex.extensionSettings().getName())))));
                exData.append(new LiteralText(" ").styled(
                        style -> style.
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(""))).
                                withUnderline(false)));
                exData.append(new LiteralText("[False]").styled(
                        style -> style.
                                withBold(!ex.extensionSettings().isEnabled()).
                                withUnderline(!ex.extensionSettings().isEnabled()).
                                withColor(Formatting.RED).
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(String.format("Disable %s", ex.extensionSettings().getName())))).
                                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/KSettings %s disable", ex.extensionSettings().getName())))));
                extensionNames.add(exData);
            }
            for (Text t : extensionNames) {
                context.getSource().sendFeedback(t, false);
            }
            return 1;
        });
        dispatcher.register(settingsCommand);
    }

    public static void onCreateDatabase() {
        db = new ServerDatabase();
        db.initializeConnection(minecraftServer.getSavePath(WorldSavePath.ROOT).toString());
        db.createPlayerTable();
        extensions.forEach(e -> e.onCreateDatabase(db.getConnection()));
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        extensions.forEach(e -> e.onServerStarted(minecraftServer));
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

    public static void onTick(MinecraftServer server) {
        extensions.forEach(e -> e.onTick(server));
        profilers.forEach(p -> {
            p.onTick(server);
            p.clearResults();
        });
    }
}
