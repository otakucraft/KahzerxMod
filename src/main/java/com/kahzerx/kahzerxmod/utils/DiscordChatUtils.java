package com.kahzerx.kahzerxmod.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
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
                emb.setDescription("**" + n + " jugadores conectados** \n\n" + msg.toString());
            } else {
                emb.setDescription(n == 0 ? "**No hay nadie online :(**" : "**" + n + " jugador conectado** \n\n" + msg.toString());
            }
            return emb;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static EmbedBuilder generateEmbed(String[] msg, String serverPrefix, boolean inline, Color color, boolean hasOne) {
        try {
            EmbedBuilder emb = new EmbedBuilder();
            emb.setColor(color);
            emb.setTitle(serverPrefix.replace("`", ""));
            if (hasOne) {
                emb.setDescription(msg[0]);
            } else {
                String[] names = new String[] {"Jugadores", "en", "Whitelist"};
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
        String msg = "[Discord] <" + event.getAuthor().getName() + "> " + event.getMessage().getContentDisplay();
        finalSendMsg(msg, server);
    }

    public static void sendMessageCrossServer(MessageReceivedEvent event, MinecraftServer server, String prefix) {
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
        finalSendMsg(msg, server);
    }

    private static void finalSendMsg(String msg, MinecraftServer server) {
        if (msg.length() >= 256) {
            msg = msg.substring(0, 253) + "...";
        }

        Matcher m = url_patt.matcher(msg);
        MutableText finalMsg = new LiteralText("");
        boolean hasUrl = false;
        int prev = 0;

        while (m.find()) {
            hasUrl = true;
            Text text = new LiteralText(m.group(0)).styled((style -> style.withColor(Formatting.GRAY)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, m.group(0)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open URL")))));
            finalMsg = finalMsg.append(new LiteralText(msg.substring(prev, m.start()))).append(text);
            prev = m.end();
        }
        if (server.getPlayerManager() != null) {
            if (hasUrl) {
                server.getPlayerManager().broadcastChatMessage(finalMsg.append(msg.substring(prev)), MessageType.CHAT, Util.NIL_UUID);
            } else {
                server.getPlayerManager().broadcastChatMessage(new LiteralText(msg), MessageType.CHAT, Util.NIL_UUID);
            }
        } else {
            LOGGER.info(String.format("Server may not be initialized yet...\nError sending %s%n", msg));
        }
    }
}
