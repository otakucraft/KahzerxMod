package com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordCommandsExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.DiscordListener;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.BanCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.ExaddCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.ExremoveCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.PardonCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.utils.DiscordUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DiscordAdminToolsExtension extends GenericExtension implements Extensions, DiscordCommandsExtension {
    private final DiscordExtension discordExtension;
    private final DiscordWhitelistExtension discordWhitelistExtension;

    private final BanCommand banCommand = new BanCommand(DiscordListener.commandPrefix);
    private final PardonCommand pardonCommand = new PardonCommand(DiscordListener.commandPrefix);
    private final ExaddCommand exaddCommand = new ExaddCommand(DiscordListener.commandPrefix);
    private final ExremoveCommand exremoveCommand = new ExremoveCommand(DiscordListener.commandPrefix);

    public DiscordAdminToolsExtension(ExtensionSettings settings, DiscordExtension discordExtension, DiscordWhitelistExtension discordWhitelistExtension) {
        super(settings);
        this.discordExtension = discordExtension;
        this.discordWhitelistExtension = discordWhitelistExtension;
    }

    public DiscordExtension getDiscordExtension() {
        return discordExtension;
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
        DiscordListener.discordExtensions.add(this);
    }

    @Override
    public DiscordAdminToolsSettings extensionSettings() {
        return (DiscordAdminToolsSettings) this.getSettings();
    }

    @Override
    public void onExtensionEnabled() {
        if (!DiscordListener.discordExtensions.contains(this)) {
            DiscordListener.discordExtensions.add(this);
        }
    }

    @Override
    public void onExtensionDisabled() {
        DiscordListener.discordExtensions.remove(this);
    }

    @Override
    public boolean processCommands(MessageReceivedEvent event, String message, MinecraftServer server) {
        if (!this.getSettings().isEnabled()) {
            return false;
        }
        if (!discordExtension.getSettings().isEnabled()) {
            return false;
        }
        if (!discordWhitelistExtension.getSettings().isEnabled()) {
            return false;
        }
        if (!DiscordUtils.isAllowed(event.getChannel().getIdLong(), this.extensionSettings().getAdminChats())) {
            if (message.startsWith(DiscordListener.commandPrefix + banCommand.getBody())
                    || message.startsWith(DiscordListener.commandPrefix + pardonCommand.getBody())
                    || message.startsWith(DiscordListener.commandPrefix + exaddCommand.getBody())
                    || message.startsWith(DiscordListener.commandPrefix + exremoveCommand.getBody())) {
                return true;
            }
        }
        if (message.startsWith(DiscordListener.commandPrefix + banCommand.getBody() + " ")) {
            banCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension, this);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + pardonCommand.getBody() + " ")) {
            pardonCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension, this);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + exaddCommand.getBody() + " ")) {
            exaddCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension, this);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + exremoveCommand.getBody() + " ")) {
            exremoveCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension, this);
            return true;
        }
        return false;
    }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
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
                then(literal("adminChats").
                        then(literal("add").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getAdminChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                context.getSource().sendFeedback(Text.literal("ID already added."), false);
                                            } else {
                                                extensionSettings().addAdminChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(Text.literal("ID added."), false);
                                                ExtensionManager.saveSettings();
                                            }
                                            return 1;
                                        }))).
                        then(literal("remove").
                                then(argument("chatID", LongArgumentType.longArg()).
                                        executes(context -> {
                                            if (extensionSettings().getAdminChats().contains(LongArgumentType.getLong(context, "chatID"))) {
                                                extensionSettings().removeAdminChatID(LongArgumentType.getLong(context, "chatID"));
                                                context.getSource().sendFeedback(Text.literal("ID removed."), false);
                                                ExtensionManager.saveSettings();
                                            } else {
                                                context.getSource().sendFeedback(Text.literal("This ID doesn't exist."), false);
                                            }
                                            return 1;
                                        }))).
                        then(literal("list").
                                executes(context -> {
                                    context.getSource().sendFeedback(Text.literal(extensionSettings().getAdminChats().toString()), false);
                                    return 1;
                                })).
                        executes(context -> {
                            String help = "ChatIDs where !ban, !pardon, !exadd and !exremove work.";
                            context.getSource().sendFeedback(Text.literal(help), false);
                            return 1;
                        }));
    }
}
