package com.kahzerx.kahzerxmod.extensions.blockInfoExtension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record LoggedBE(BlockPos pos, World world) {
    public BlockPos getPos() {
        return pos;
    }

    public World getWorld() {
        return world;
    }
}
