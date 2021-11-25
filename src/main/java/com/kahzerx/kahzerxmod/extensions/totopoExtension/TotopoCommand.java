package com.kahzerx.kahzerxmod.extensions.totopoExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.awt.*;

import static net.minecraft.server.command.CommandManager.literal;

public class TotopoCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, TotopoExtension totopo) {
        dispatcher.register(literal("totopo").
                requires(server -> totopo.extensionSettings().isEnabled()).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();
                    totopo.totopoShape(player, context.getSource().getWorld(), x, y, z);
                    return 1;
                }));
    }
}
