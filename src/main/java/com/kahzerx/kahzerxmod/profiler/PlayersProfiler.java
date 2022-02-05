package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.PlayerInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayersProfiler extends AbstractProfiler {
    @Override
    public void onTick(MinecraftServer server) {
        List<PlayerInstance> playerList = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                playerList.add(new PlayerInstance(player.getName().getString(), player.getUuidAsString(), world.getRegistryKey().getValue().getPath(), player.getX(), player.getY(), player.getZ()));
            }
        }

        HashMap<String, Object> players = new HashMap<>();
        players.put("online_players", playerList);
        this.addResult(server.getTicks(), players);
    }
}
