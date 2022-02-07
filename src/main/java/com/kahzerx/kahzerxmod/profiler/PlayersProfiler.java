package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.PlayersInstance;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class PlayersProfiler extends AbstractProfiler {
    @Override
    public void onTick(MinecraftServer server, String id) {
        List<PlayersInstance> playerList = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                playerList.add(new PlayersInstance(player.getName().getString(), player.getUuidAsString(), world.getRegistryKey().getValue().getPath(), player.getX(), player.getY(), player.getZ()));
            }
        }
        playerList.add(new PlayersInstance("", "", "", 0.0D, 0.0D, 0.0D));

        this.addResult(server.getTicks(), new ProfilerResult("online_players", id, playerList));
    }
}
