package com.kahzerx.kahzerxmod.extensions.discordExtension.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordChatUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern url_patt = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

    public static EmbedBuilder generateEmbed(StringBuilder msg, int n, String prefix) {
        try {
            EmbedBuilder emb = new EmbedBuilder();
            emb.setColor(n != 0 ? Color.GREEN : Color.RED);
            emb.setTitle(prefix.replace("`", ""));
            if (n > 1) {
                emb.setDescription("**" + n + " online players** \n\n" + msg.toString());
            } else {
                emb.setDescription(n == 0 ? "**no players online :(**" : "**" + n + " player online** \n\n" + msg.toString());
            }
            return emb;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static EmbedBuilder generateEmbed(String[] msg, String serverPrefix, boolean inline, Color color, boolean hasOne, boolean shouldFeedback) {
        if (!shouldFeedback) {
            return null;
        }
        try {
            EmbedBuilder emb = new EmbedBuilder();
            emb.setColor(color);
            if (!serverPrefix.equals("")) {
                emb.setTitle(serverPrefix.replace("`", ""));
            }
            if (hasOne) {
                emb.setDescription(msg[0].replace("_", "\\_"));
            } else {
                String[] names = new String[] {"Players", "in", "Whitelist"};
                HashMap<String, List<String>> columns = new HashMap<>();
                columns.put(names[0], new ArrayList<>());
                columns.put(names[1], new ArrayList<>());
                columns.put(names[2], new ArrayList<>());
                int i = 0;
                while (i < msg.length) {
                    for (String n : names) {
                        if (i < msg.length) {
                            columns.get(n).add(msg[i].replace("_", "\\_"));
                            i++;
                        }
                    }
                }
                for (String n : names) {
                    emb.addField(n, String.join("\n", columns.get(n)), inline);
                }
            }
            return emb;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void sendMessage(MessageReceivedEvent event, MinecraftServer server) {
        finalSendMsg(event.getMessage().getContentDisplay(), server, event);
    }

    public static void sendMessageCrossServer(MessageReceivedEvent event, MinecraftServer server, String prefix) {
        if (prefix.equals("")) {
            return;
        }

        Matcher m = Pattern.compile("^(`<|:)(\\w+)(>`|:)").matcher(event.getMessage().getContentDisplay());
        if (m.find()) {  // El mensaje recibido no tiene prefix.
            return;
        }

        prefix = prefix.strip();
        String msg = "[" + prefix + "] " + event.getMessage().getContentDisplay().replace("`", "");
        msg = msg.substring(msg.indexOf(" ") + 1);
        if (msg.split(" ")[0].equals(prefix.replace("`", ""))) {
            return;  // msg from the same server.
        }
        msg = msg.replaceAll("\\:([^\\}]+)\\:", "");
        msg = msg.replace("*", "");
        msg = msg.replace("\\", "");
        String[] tMsg = Arrays.stream(msg.split(" ")).filter(x -> !x.isEmpty()).toArray(String[]::new);
        msg = String.join(" ", tMsg);
        finalSendMsg(msg, server, null);
    }

    private static void finalSendMsg(String msg, MinecraftServer server, MessageReceivedEvent fromDiscordUser) {
        if (msg.length() >= 256) {
            msg = msg.substring(0, 253) + "...";
        }

        Matcher m = url_patt.matcher(msg);
        MutableText finalMsg = Text.literal("");
        if (fromDiscordUser != null) {
            finalMsg.append(Text.literal("[Discord]").styled(style -> style.withColor(Formatting.GRAY)));
            finalMsg.append(Text.literal(" ").styled(style -> style.withColor(Formatting.RESET)));
            MutableText hover = Text.literal(String.format("Author: %s\n", fromDiscordUser.getAuthor().getName()));
            if (fromDiscordUser.getMember() != null && fromDiscordUser.getMember().getNickname() != null) {
                hover.append(Text.literal(String.format("NickName: %s\n", fromDiscordUser.getMember().getNickname())));
            }
            hover.append(Text.literal(String.format("UserID: %s\n", fromDiscordUser.getAuthor().getId())));
            hover.append(Text.literal(String.format("MessageID: %s\nChannelID: %s", fromDiscordUser.getMessageId(), fromDiscordUser.getChannel().getId())));
            finalMsg.append(Text.literal("<" + fromDiscordUser.getAuthor().getName() + ">").styled(style -> style.withHoverEvent(
                    new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            hover
                    )
            )));
            finalMsg.append(Text.literal(" ").styled(style -> style.withColor(Formatting.RESET)));
        }
        boolean hasUrl = false;
        int prev = 0;

        while (m.find()) {
            hasUrl = true;
            Text text = Text.literal(m.group(0)).styled((style -> style.withColor(Formatting.GRAY)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, m.group(0)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Open URL")))));
            finalMsg = finalMsg.append(Text.literal(msg.substring(prev, m.start()))).append(text);
            prev = m.end();
        }
        if (server.getPlayerManager() != null) {
            if (hasUrl) {
                server.getPlayerManager().broadcast(finalMsg.append(msg.substring(prev)), MessageType.SYSTEM);
            } else {
                server.getPlayerManager().broadcast(finalMsg.append(Text.literal(msg)), MessageType.SYSTEM);
            }
        } else {
            LOGGER.info(String.format("Server may not be initialized yet...\nError sending %s%n", msg));
        }
    }
}
