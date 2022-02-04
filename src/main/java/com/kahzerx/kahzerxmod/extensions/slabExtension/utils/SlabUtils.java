package com.kahzerx.kahzerxmod.extensions.slabExtension.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlabUtils {
    public static boolean isSlab(Item item) {
        return Block.getBlockFromItem(item) instanceof SlabBlock;
    }

    public static void flipSlab(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(SlabBlock.TYPE, world.getBlockState(pos).get(SlabBlock.TYPE) == SlabType.DOUBLE ? SlabType.DOUBLE : SlabType.TOP));
    }
}
