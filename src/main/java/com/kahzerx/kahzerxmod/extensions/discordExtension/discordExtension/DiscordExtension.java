package com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.KahzerxServer;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordSendCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.utils.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

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
                                                context.getSource().sendFeedback(new LiteralText("Detén el bot antes de hacer cambios..."), false);
                                            } else {
                                                extensionSettings().setToken(StringArgumentType.getString(context, "token"));
                                                extensionSettings().setChatChannelID(LongArgumentType.getLong(context, "chatChannelID"));
                                                context.getSource().sendFeedback(new LiteralText("Nuevo bot configurado!"), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        })))).
                then(literal("stop").
                        executes(context -> {
                            if (DiscordListener.chatbridge) {
                                DiscordListener.stop();
                                this.extensionSettings().setRunning(false);
                                context.getSource().sendFeedback(new LiteralText("Bot detenido!"), false);
                                ExtensionManager.saveSettings();
                            } else {
                                context.getSource().sendFeedback(new LiteralText("El bot ya estaba detenido."), false);
                            }
                            return 1;
                        })).
                then(literal("start").
                        executes(context -> {
                            if (!DiscordListener.chatbridge) {
                                DiscordListener.start(KahzerxServer.minecraftServer, extensionSettings().getToken(), String.valueOf(extensionSettings().getChatChannelID()), this);
                                if (DiscordListener.chatbridge) {
                                    context.getSource().sendFeedback(new LiteralText("Bot iniciado!"), false);
                                } else {
                                    context.getSource().sendFeedback(new LiteralText("Fallo al iniciar el Bot."), false);
                                }
                                ExtensionManager.saveSettings();
                            } else {
                                context.getSource().sendFeedback(new LiteralText("El bot ya estaba en ejecución."), false);
                            }
                            return 1;
                        })).
                then(literal("chatBridgePrefix").
                        then(argument("prefix", StringArgumentType.string()).
                                executes(context -> {
                                    extensionSettings().setPrefix(StringArgumentType.getString(context, "prefix"));
                                    context.getSource().sendFeedback(new LiteralText("[Prefix] > " + extensionSettings().getPrefix() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Prefix que aparecerá en Discord delante de todos los mensajes que vengan de este server.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            String prefix = extensionSettings().getPrefix();
                            context.getSource().sendFeedback(new LiteralText(prefix.equals("") ? "No hay prefix." : "[Prefix] > " + extensionSettings().getPrefix() + "."), false);
                            return 1;
                        })).
                then(literal("crossServerChat").
                        then(argument("enabled", BoolArgumentType.bool()).
                                executes(context -> {
                                    extensionSettings().setCrossServerChat(BoolArgumentType.getBool(context, "enabled"));
                                    context.getSource().sendFeedback(new LiteralText("[CrossServerChat] > " + extensionSettings().isCrossServerChat() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Todos los servers con el mismo bot conectado para el chatbridge pero distinto prefix puedan intercambiar mensajes redirigiendo desde Discord.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            context.getSource().sendFeedback(new LiteralText("[CrossServerChat] > " + extensionSettings().isCrossServerChat() + "."), false);
                            return 1;
                        })).
                then(literal("allowedChats").
                        then(literal("add").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getAllowedChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                context.getSource().sendFeedback(new LiteralText("Este ID ya está añadido."), false);
                                            } else {
                                                extensionSettings().addAllowedChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(new LiteralText("ID añadido."), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        }))).
                        then(literal("remove").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getAllowedChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                extensionSettings().removeAllowedChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(new LiteralText("ID eliminado."), false);
                                                ExtensionManager.saveSettings();
                                            } else {
                                                context.getSource().sendFeedback(new LiteralText("Este ID no estaba añadido."), false);
                                            }
                                            return 1;
                                        }))).
                        then(literal("list").
                                executes(context -> {
                                    context.getSource().sendFeedback(new LiteralText(extensionSettings().getAllowedChats().toString()), false);
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Lista de ChatIDs en los que el comando !online funciona.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            return 1;
                        }));
    }
}
