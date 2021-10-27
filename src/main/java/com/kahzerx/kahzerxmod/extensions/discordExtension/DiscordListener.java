package com.kahzerx.kahzerxmod.extensions.discordExtension;

import com.kahzerx.kahzerxmod.extensions.discordExtension.commands.OnlineCommand;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordExtension;
import com.kahzerx.kahzerxmod.extensions.discordExtension.discordExtension.DiscordSettings;
import com.kahzerx.kahzerxmod.utils.DiscordChatUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    public static List<DiscordCommandsExtension> discordExtensions = new ArrayList<>();
    public static final String commandPrefix = "!";

    public static JDA jda = null;
    private static String channelId = "";
    private static String token = "";
    public static boolean chatbridge = false;
    private static DiscordSettings discordSettings = null;

    private final MinecraftServer server;

    private final OnlineCommand onlineCommand = new OnlineCommand(commandPrefix);

    public DiscordListener(MinecraftServer server) {
        this.server = server;
    }

    public static void start(MinecraftServer server, String t, String channel, DiscordExtension discordExtension) {
        channelId = channel;
        token = t;
        discordSettings = discordExtension.extensionSettings();
        try {
            discordExtension.extensionSettings().setRunning(false);
            chatbridge = false;
            jda = JDABuilder.createDefault(t).addEventListeners(new DiscordListener(server)).build();
            jda.awaitReady();
            discordExtension.extensionSettings().setRunning(true);
            chatbridge = true;
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("REMEMBER TO SET A BOT CORRECTLY!");
        }
    }

    public static void stop() {
        jda.shutdownNow();
        chatbridge = false;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!chatbridge) {
            return;
        }
        if (event.getMessage().getContentDisplay().equals("")) {
            return;
        }
        if (event.getMessage().getContentRaw().equals("")) {
            return;
        }
        if (event.getAuthor().isBot()) {
            if (!discordSettings.isCrossServerChat()) {
                return;
            }
            if (event.getAuthor().getIdLong() != jda.getSelfUser().getIdLong()) {
                return;
            }
        }

        String message = event.getMessage().getContentRaw();
        if (message.equals(commandPrefix + onlineCommand.getBody())) {
            onlineCommand.execute(event, server, discordSettings.getPrefix(), discordSettings.getAllowedChats());
            return;
        }

        for (DiscordCommandsExtension extension : discordExtensions) {
            if (extension.processCommands(event, message, server)) {
                return;
            }
        }

        if (event.getChannel().getIdLong() == discordSettings.getChatChannelID()) {
            if (event.getAuthor().getIdLong() == jda.getSelfUser().getIdLong()) {
                if (discordSettings.isCrossServerChat()) {
                    DiscordChatUtils.sendMessageCrossServer(event, server, discordSettings.getPrefix());
                }
            } else {
                DiscordChatUtils.sendMessage(event, server);
            }
        }
    }

    public static void sendDiscordMessage(String msg) {
        if (!chatbridge) {
            return;
        }
        TextChannel ch = jda.getTextChannelById(channelId);
        if (ch != null) {
            ch.sendMessage(discordSettings.getPrefix() + " " + msg).queue();
        } else {
            System.out.println("Unable to find this Text Channel.");
        }
    }
}
