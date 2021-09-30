package com.kahzerx.kahzerxmod.utils;

import com.google.common.collect.Sets;
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
}
