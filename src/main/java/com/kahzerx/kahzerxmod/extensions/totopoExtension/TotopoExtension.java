package com.kahzerx.kahzerxmod.extensions.totopoExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.awt.*;

public class TotopoExtension extends GenericExtension implements Extensions {
    public TotopoExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new TotopoCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public void totopoShape(ServerPlayerEntity player, ServerWorld world, double pX, double pY, double pZ) {
        int abovePlayer = 7;
        float rows = 6;
        pY += abovePlayer;
        float playerYaw = player.getYaw();
        double finalPY = pY;
        new Thread(() -> {
            for (int k = 0; k < 5; k++) {
                spawnDustParticle(world, pX, finalPY, pZ);
                spawnDustParticle(world, pX, finalPY - ((rows - 1) - (rows - 1) / 8), pZ);
                boolean n = (playerYaw < 45 && playerYaw >= -45) || (playerYaw > 135 || playerYaw <= -135);
                for (float i = 1; i < rows; i++) {
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double x = n ? pX + (i * 0.5) : pX;
                    double z = n ? pZ : pZ + (i * 0.5);
                    spawnDustParticle(world, x, finalPY - (i - i / 8), z);
                    spawnDustParticle(world, x, finalPY - ((rows - 1) - (rows - 1) / 8), z);

                    float j = i - 0.5F;

                    x = n ? pX + (j * 0.5) : pX;
                    z = n ? pZ : pZ + (j * 0.5);
                    spawnDustParticle(world, x, finalPY - (j - j / 8), z);

                    double x1 = n ? pX - (i * 0.5) : pX;
                    double z1 = n ? pZ : pZ - (i * 0.5);
                    spawnDustParticle(world, x1, finalPY - (i - i / 8), z1);
                    spawnDustParticle(world, x1, finalPY - ((rows - 1) - (rows - 1) / 8), z1);

                    x1 = n ? pX - (j * 0.5) : pX;
                    z1 = n ? pZ : pZ - (j * 0.5);
                    spawnDustParticle(world, x1, finalPY - (j - j / 8), z1);
                }
                try {
                    Thread.sleep(600L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void spawnDustParticle(ServerWorld world, double x, double y, double z) {
        world.spawnParticles(
                new DustParticleEffect(
                        new Vec3f(
                                Vec3d.unpackRgb(
                                        new Color(255, 220, 0).getRGB()
                                )
                        ), 1.0F
                ),
                x,
                y,
                z,
                50,
                0,
                0,
                0,
                1
        );
    }
}
