package com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistSyncExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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
    public void onExtensionEnabled() { }

    @Override
    public void onExtensionDisabled() { }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
                then(literal("notifyChatID").
                        then(argument("chatID", LongArgumentType.longArg()).
                                executes(context -> {
                                    extensionSettings().setNotifyChannelID(LongArgumentType.getLong(context, "chatID"));
                                    context.getSource().sendFeedback(new LiteralText("[notifyChatID] > " + extensionSettings().getNotifyChannelID() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Canal en el que se notifica cuando alguien es eliminado de la whitelist porque ya no tenga el rol.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            context.getSource().sendFeedback(new LiteralText("[notifyChatID] > " + extensionSettings().getNotifyChannelID() + "."), false);
                            return 1;
                        })).
                then(literal("groupID").
                        then(argument("groupID", LongArgumentType.longArg()).
                                executes(context -> {
                                    extensionSettings().setGroupID(LongArgumentType.getLong(context, "groupID"));
                                    context.getSource().sendFeedback(new LiteralText("[groupID] > " + extensionSettings().getGroupID() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "ID del servidor de discord o guild, para saber de qué server mirar los roles de los members.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            context.getSource().sendFeedback(new LiteralText("[groupID] > " + extensionSettings().getGroupID() + "."), false);
                            return 1;
                        })).
                then(literal("aggressive").
                        then(argument("aggressive", BoolArgumentType.bool()).
                                executes(context -> {
                                    extensionSettings().setAggressive(BoolArgumentType.getBool(context, "aggressive"));
                                    context.getSource().sendFeedback(new LiteralText("[aggressive] > " + extensionSettings().isAggressive() + "."), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Checks extra de si debería sacar a la gente de la whitelist que ya estaba de antes o no está 100% en la base de datos, sincronización completa.";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            context.getSource().sendFeedback(new LiteralText("[aggressive] > " + extensionSettings().isAggressive() + "."), false);
                            return 1;
                        })).
                then(literal("validRoles").
                        then(literal("add").
                                then(argument("roleID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getValidRoles().contains(LongArgumentType.getLong(context, "roleID"))) {
                                                context.getSource().sendFeedback(new LiteralText("Este ID ya está añadido."), false);
                                            } else {
                                                extensionSettings().addValidRoleID(LongArgumentType.getLong(context, "roleID"));
                                                context.getSource().sendFeedback(new LiteralText("ID añadido."), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        }))).
                        then(literal("remove").
                                then(argument("roleID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getValidRoles().contains(LongArgumentType.getLong(context, "roleID"))) {
                                                extensionSettings().removeValidRoleID(LongArgumentType.getLong(context, "roleID"));
                                                context.getSource().sendFeedback(new LiteralText("ID eliminado."), false);
                                                ExtensionManager.saveSettings();
                                            } else {
                                                context.getSource().sendFeedback(new LiteralText("Este ID no estaba añadido."), false);
                                            }
                                            return 1;
                                        }))).
                        then(literal("list").
                                executes(context -> {
                                    context.getSource().sendFeedback(new LiteralText(extensionSettings().getValidRoles().toString()), false);
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "Lista de roles que un member tiene que tener en discord (al menos 1) para que lo le saque de la whitelist(ej: rol de sub).";
                            context.getSource().sendFeedback(new LiteralText(help), false);
                            return 1;
                        }));
    }
}
