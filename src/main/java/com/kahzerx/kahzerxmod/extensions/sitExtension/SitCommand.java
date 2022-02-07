package com.kahzerx.kahzerxmod.extensions.sitExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class SitCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SitExtension extension) {
        dispatcher.register(literal("sit").
                requires(server -> extension.extensionSettings().isEnabled()).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    SitEntity sitEntity = new SitEntity(player.getWorld(), player.getX(), player.getY() - 0.16, player.getZ());
                    if (!player.isOnGround()) {
                        return 1;
                    }
                    sitEntity.setSitEntity(true);
                    player.getWorld().spawnEntity(sitEntity);
                    player.setSneaking(false);
                    player.startRiding(sitEntity);
                    player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(player.getId(), player.getDataTracker(), true));
                    return 1;
                }));
    }
}
