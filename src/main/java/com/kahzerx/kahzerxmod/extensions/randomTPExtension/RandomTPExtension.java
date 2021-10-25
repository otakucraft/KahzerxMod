package com.kahzerx.kahzerxmod.extensions.randomTPExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;

public class RandomTPExtension extends GenericExtension implements Extensions {
    public RandomTPExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new RandomTPCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public int tpAndSpawnPoint(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return 1;
        }
        if (player.getEntityWorld().getRegistryKey() != World.OVERWORLD) {
            source.sendFeedback(new LiteralText("Solo puedes usar este comando en el Overworld."), false);
            return 1;
        }
        final int min = -10000;
        final int max = 10000;

        Random rand = new Random();
        double x = min + (max + max) * rand.nextDouble();
        double z = min + (max + max) * rand.nextDouble();
        final double y = 255;

        player.teleport(x, y, z);
        BlockPos pos1 = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, y, z));
        BlockPos posBelow = new BlockPos(pos1.getX(), pos1.getY() - 1, pos1.getZ());
        if (source.getWorld().getBlockState(posBelow).getBlock().equals(Blocks.WATER) ||
                source.getWorld().getBlockState(posBelow).getBlock().equals(Blocks.LAVA)) {
            tpAndSpawnPoint(source);
        } else {
            player.teleport(x, pos1.getY(), z);
            player.setSpawnPoint(
                    player.getEntityWorld().getRegistryKey(),
                    player.getBlockPos(),
                    0.0F,
                    true,
                    false
            );
        }
        return 1;
    }
}
