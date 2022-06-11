package com.kahzerx.kahzerxmod;

import com.kahzerx.kahzerxmod.database.ServerDatabase;
import com.kahzerx.kahzerxmod.utils.FileUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class KahzerxServer {
    public static MinecraftServer minecraftServer;
    public static List<Extensions> extensions = new ArrayList<>();
//    public static List<AbstractProfiler> profilers = new ArrayList<>();
    public static ServerDatabase db = new ServerDatabase();
    public static CommandDispatcher<ServerCommandSource> dispatcher;
    public static CommandRegistryAccess commandRegistryAccess;

    public static void onRunServer(MinecraftServer minecraftServer) {
//        profilers.add(new ChunkProfiler());
//        profilers.add(new BlockEntitiesProfiler());
//        profilers.add(new EntityProfiler());
//        profilers.add(new MSPTProfiler());
//        profilers.add(new PlayersProfiler());
//        profilers.add(new RamProfiler());
//        profilers.add(new TPSProfiler());
        KahzerxServer.minecraftServer = minecraftServer;
        ExtensionManager.manageExtensions(FileUtils.loadConfig(minecraftServer.getSavePath(WorldSavePath.ROOT).toString()));
        Collections.sort(extensions);

        extensions.forEach(e -> e.onServerRun(minecraftServer));

        ExtensionManager.saveSettings();

        extensions.forEach(e -> e.onRegisterCommands(dispatcher));
        extensions.forEach(e -> e.onRegisterCommands(dispatcher, commandRegistryAccess));

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
                        context.getSource().sendFeedback(
                                Text.literal(ex.extensionSettings().getName() + "\n").styled(style -> style.withBold(true)).
                                        append(MarkEnum.INFO.appendMessage(ex.extensionSettings().getDescription() + "\n", Formatting.GRAY).styled(style -> style.withBold(false))).
                                        append(Text.literal("Enabled: ").styled(style -> style.withBold(false).withColor(Formatting.WHITE))).
                                        append(Text.literal(String.format("%s", ex.extensionSettings().isEnabled())).styled(style -> style.withBold(false).withColor(ex.extensionSettings().isEnabled() ? Formatting.GREEN : Formatting.RED))), false);
                        return 1;
                    });
            ex.settingsCommand(extensionSubCommand);  // Otros ajustes por si fueran necesarios para las extensiones más complejas.
            settingsCommand.then(extensionSubCommand);
        }
        settingsCommand.executes(context -> {
            List<MutableText> extensionNames = new ArrayList<>();
            for (Extensions ex : extensions) {
                MutableText exData = Text.literal("- " + ex.extensionSettings().getName() + " ").styled(
                        style -> style.
                                withBold(false).
                                withUnderline(false).
                                withColor(Formatting.WHITE).
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(ex.extensionSettings().getDescription()))));
                exData.append(Text.literal("[True]").styled(
                        style -> style.
                                withBold(false).
                                withUnderline(ex.extensionSettings().isEnabled()).
                                withColor(Formatting.GREEN).
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(String.format("Enable %s", ex.extensionSettings().getName())))).
                                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/KSettings %s enable", ex.extensionSettings().getName())))));
                exData.append(Text.literal(" ").styled(
                        style -> style.
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(""))).
                                withUnderline(false)));
                exData.append(Text.literal("[False]").styled(
                        style -> style.
                                withBold(false).
                                withUnderline(!ex.extensionSettings().isEnabled()).
                                withColor(Formatting.RED).
                                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(String.format("Disable %s", ex.extensionSettings().getName())))).
                                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/KSettings %s disable", ex.extensionSettings().getName())))));
                extensionNames.add(exData);
            }
            context.getSource().sendFeedback(Text.literal("All Settings").styled(style -> style.withBold(true)), false);
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
        extensions.forEach(e -> e.onCreateDatabase(minecraftServer.getSavePath(WorldSavePath.ROOT).toString()));
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        extensions.forEach(e -> e.onServerStarted(minecraftServer));
    }

    public static void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        KahzerxServer.dispatcher = dispatcher;
        KahzerxServer.commandRegistryAccess = commandRegistryAccess;

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

    public static void onPlayerBreakBlock(ServerPlayerEntity player, World world, BlockPos pos) {
        extensions.forEach(e -> e.onPlayerBreakBlock(player, world, pos));
    }

    public static void onPlayerPlaceBlock(ServerPlayerEntity player, World world, BlockPos pos) {
        extensions.forEach(e -> e.onPlayerPlaceBlock(player, world, pos));
    }

    public static void onChatMessage(ServerPlayerEntity player, String chatMessage) {
        extensions.forEach(e -> e.onChatMessage(player, chatMessage));
    }

    public static void onAdvancement(String advancement) {
        extensions.forEach(e -> e.onAdvancement(advancement));
    }

    public static void onClick(ServerPlayerEntity player) {
        extensions.forEach(e -> e.onClick(player));
    }

    public static void onTick(MinecraftServer server) {
        if (server.getTicks() < 20) {
            return;
        }
        extensions.forEach(e -> e.onTick(server));
//        String id = DateUtils.getAcc();
//        profilers.forEach(p -> {
//            p.onTick(server, id);
//            p.clearResults();
//        });
    }
}
