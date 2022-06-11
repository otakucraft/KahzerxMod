package com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.KahzerxServer;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordSendCommand;
import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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
    }

    @Override
    public void onServerStarted(MinecraftServer minecraftServer) {
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
        boolean isBot = player.getClass() == KlonePlayerEntity.class;
        DiscordListener.sendDiscordMessage(":arrow_right: **" + player.getName().getString().replace("_", "\\_") + (isBot ? " [Bot]" : "") + " joined the game!**");
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        boolean isBot = player.getClass() == KlonePlayerEntity.class;
        DiscordListener.sendDiscordMessage(":arrow_left: **" + player.getName().getString().replace("_", "\\_") + (isBot ? " [Bot]" : "") + " left the game!**");
    }

    @Override
    public void onPlayerDied(ServerPlayerEntity player) {
        boolean isBot = player.getClass() == KlonePlayerEntity.class;
        DiscordListener.sendDiscordMessage(":skull_crossbones: **" + player.getDamageTracker().getDeathMessage().getString().replace("_", "\\_") + (isBot ? " [Bot]" : "") + "**");
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
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new DiscordSendCommand().register(dispatcher, this);
    }

    @Override
    public void onExtensionEnabled() {
        if (!extensionSettings().isEnabled()) {
            return;
        }
        DiscordListener.start(KahzerxServer.minecraftServer, extensionSettings().getToken(), String.valueOf(extensionSettings().getChatChannelID()), this);
        DiscordListener.sendDiscordMessage("\\o/");
        PlayerUtils.reloadCommands();
    }

    @Override
    public void onExtensionDisabled() {
        if (extensionSettings().isRunning()) {
            DiscordListener.stop();
        }
        this.extensionSettings().setRunning(false);
        PlayerUtils.reloadCommands();
    }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
                then(literal("setBot").
                        then(argument("token", StringArgumentType.string()).
                                then(argument("chatChannelID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (DiscordListener.chatbridge) {
                                                context.getSource().sendFeedback(Text.literal("Stop the bot before you make any changes..."), false);
                                            } else {
                                                extensionSettings().setToken(StringArgumentType.getString(context, "token"));
                                                extensionSettings().setChatChannelID(LongArgumentType.getLong(context, "chatChannelID"));
                                                context.getSource().sendFeedback(Text.literal("Done!"), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        })))).
                then(literal("stop").
                        executes(context -> {
                            if (DiscordListener.chatbridge) {
                                DiscordListener.stop();
                                this.extensionSettings().setRunning(false);
                                context.getSource().sendFeedback(Text.literal("Bot stopped!"), false);
                                ExtensionManager.saveSettings();
                            } else {
                                context.getSource().sendFeedback(Text.literal("Bot already stopped."), false);
                            }
                            return 1;
                        })).
                then(literal("start").
                        executes(context -> {
                            if (!DiscordListener.chatbridge) {
                                DiscordListener.start(KahzerxServer.minecraftServer, extensionSettings().getToken(), String.valueOf(extensionSettings().getChatChannelID()), this);
                                if (DiscordListener.chatbridge) {
                                    context.getSource().sendFeedback(Text.literal("Started!"), false);
                                } else {
                                    context.getSource().sendFeedback(Text.literal("Failed to start."), false);
                                }
                                ExtensionManager.saveSettings();
                            } else {
                                context.getSource().sendFeedback(Text.literal("But is already running."), false);
                            }
                            return 1;
                        })).
                then(literal("shouldFeedback").
                        then(argument("feedback", BoolArgumentType.bool()).
                                executes(context -> {
                                    extensionSettings().setShouldFeedback(BoolArgumentType.getBool(context, "feedback"));
                                    context.getSource().sendFeedback(Text.literal("[shouldFeedback] > " + extensionSettings().isShouldFeedback() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            context.getSource().sendFeedback(Text.literal("[shouldFeedback] > " + extensionSettings().isShouldFeedback() + "."), false);
                            return 1;
                        })).
                then(literal("chatBridgePrefix").
                        then(argument("prefix", StringArgumentType.string()).
                                executes(context -> {
                                    extensionSettings().setPrefix(StringArgumentType.getString(context, "prefix"));
                                    context.getSource().sendFeedback(Text.literal("[Prefix] > " + extensionSettings().getPrefix() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Server prefix.";
                            context.getSource().sendFeedback(Text.literal(help), false);
                            String prefix = extensionSettings().getPrefix();
                            context.getSource().sendFeedback(Text.literal(prefix.equals("") ? "There is no prefix." : "[Prefix] > " + extensionSettings().getPrefix() + "."), false);
                            return 1;
                        })).
                then(literal("crossServerChat").
                        then(argument("enabled", BoolArgumentType.bool()).
                                executes(context -> {
                                    extensionSettings().setCrossServerChat(BoolArgumentType.getBool(context, "enabled"));
                                    context.getSource().sendFeedback(Text.literal("[CrossServerChat] > " + extensionSettings().isCrossServerChat() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Same bot(same token) on same chatID and different prefix on many servers will connect their chats.";
                            context.getSource().sendFeedback(Text.literal(help), false);
                            context.getSource().sendFeedback(Text.literal("[CrossServerChat] > " + extensionSettings().isCrossServerChat() + "."), false);
                            return 1;
                        })).
                then(literal("allowedChats").
                        then(literal("add").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getAllowedChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                context.getSource().sendFeedback(Text.literal("ID already added."), false);
                                            } else {
                                                extensionSettings().addAllowedChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(Text.literal("ID added."), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        }))).
                        then(literal("remove").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getAllowedChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                extensionSettings().removeAllowedChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(Text.literal("ID removed."), false);
                                                ExtensionManager.saveSettings();
                                            } else {
                                                context.getSource().sendFeedback(Text.literal("This ID doesn't exist."), false);
                                            }
                                            return 1;
                                        }))).
                        then(literal("list").
                                executes(context -> {
                                    context.getSource().sendFeedback(Text.literal(extensionSettings().getAllowedChats().toString()), false);
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "ChatIDs where !online work.";
                            context.getSource().sendFeedback(Text.literal(help), false);
                            return 1;
                        }));
    }
}
