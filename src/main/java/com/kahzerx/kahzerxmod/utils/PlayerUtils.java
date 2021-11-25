package com.kahzerx.kahzerxmod.utils;

import com.google.common.collect.Sets;
import com.kahzerx.kahzerxmod.KahzerxServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Set;

public class PlayerUtils {
    public static String getPlayerWithColor(final ServerPlayerEntity player) {
        return Formatting.YELLOW + player.getName().asString();
    }

    public static Collection<String> getPlayers(ServerCommandSource source) {
        Set<String> players = Sets.newLinkedHashSet();
        players.addAll(source.getPlayerNames());
        return players;
    }

    public static void reloadCommands() {
        MinecraftServer server = KahzerxServer.minecraftServer;
        server.send(new ServerTask(server.getTicks(), () -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                server.getCommandManager().sendCommandTree(player);
            }
        }));
    }
}
