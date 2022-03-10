package com.kahzerx.kahzerxmod.extensions.antiXRayExtension;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.ArrayList;

public class XRayBlocks {
    public static final ArrayList<BlockState> BLACKLISTED_BLOCKS = new ArrayList<>();
    public static final ArrayList<BlockState> WHITELISTED_BLOCKS = new ArrayList<>();
    static {
        BLACKLISTED_BLOCKS.add(Blocks.DIAMOND_BLOCK.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.IRON_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.REDSTONE_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.GOLD_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.LAPIS_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.COPPER_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.EMERALD_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.COAL_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.NETHER_GOLD_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE.getDefaultState());
        BLACKLISTED_BLOCKS.add(Blocks.ANCIENT_DEBRIS.getDefaultState());

        WHITELISTED_BLOCKS.add(Blocks.STONE.getDefaultState());
        WHITELISTED_BLOCKS.add(Blocks.DEEPSLATE.getDefaultState());
        WHITELISTED_BLOCKS.add(Blocks.DIORITE.getDefaultState());
        WHITELISTED_BLOCKS.add(Blocks.ANDESITE.getDefaultState());
    }

    public static void noop() { }
}
