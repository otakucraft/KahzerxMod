package com.kahzerx.kahzerxmod.mixin.slabExtension;

import com.kahzerx.kahzerxmod.extensions.slabExtension.SlabExtension;
import com.kahzerx.kahzerxmod.extensions.slabExtension.utils.SlabUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlabBlock.class)
public class SlabBlockExtension extends Block {
    public SlabBlockExtension(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (SlabExtension.isExtensionEnabled && SlabUtils.isSlab(itemStack.getItem()) && itemStack.hasEnchantments()) {
            SlabUtils.flipSlab(world, pos, state);
        }
    }
}
