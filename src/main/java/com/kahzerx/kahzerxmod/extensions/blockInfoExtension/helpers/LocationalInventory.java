package com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LocationalInventory {
    BlockPos getLoc();
    World getInvWorld();
}
