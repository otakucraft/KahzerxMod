package com.kahzerx.kahzerxmod.extensions.blockInfoExtension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LocationalInventory {
    BlockPos getLoc();
    World getInvWorld();
}
