package com.kahzerx.kahzerxmod.extensions.discordExtension.discordAdminToolsExtension;

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
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import com.kahzerx.kahzerxmod.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.minecraft.server.MinecraftServer;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

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

    }

    @Override
    public void onExtensionDisabled() {

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
                event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
                EmbedBuilder embed = DiscordChatUtils.generateEmbed(
                        new String[]{"**Usa bien los canales!!! >:(**"},
                        discordExtension.extensionSettings().getPrefix(),
                        true,
                        Color.RED,
                        true
                );
                assert embed != null;
                MessageAction embedSent = event.getChannel().sendMessageEmbeds(embed.build());
                embedSent.queue(m -> m.delete().queueAfter(2, TimeUnit.SECONDS));
                return true;
            }
        }
        if (message.startsWith(DiscordListener.commandPrefix + banCommand.getBody() + " ")) {
            banCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + pardonCommand.getBody() + " ")) {
            pardonCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + exaddCommand.getBody() + " ")) {
            exaddCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension);
            return true;
        } else if (message.startsWith(DiscordListener.commandPrefix + exremoveCommand.getBody() + " ")) {
            exremoveCommand.execute(event, server, discordExtension.extensionSettings().getPrefix(), discordWhitelistExtension);
            return true;
        }
        return false;
    }
}
