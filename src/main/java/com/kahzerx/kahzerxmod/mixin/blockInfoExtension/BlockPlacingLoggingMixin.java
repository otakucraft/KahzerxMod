package com.kahzerx.kahzerxmod.mixin.blockInfoExtension;

import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockActionLog;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockInfoExtension;
import com.kahzerx.kahzerxmod.utils.DateUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class BlockPlacingLoggingMixin {
    @Redirect(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void onBroken(Block block, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        block.onPlaced(world, pos, state, placer, itemStack);
        if (placer instanceof ServerPlayerEntity) {
            BlockActionLog actionLog = new BlockActionLog(
                    placer.getName().getString(),
                    state.getBlock().getTranslationKey(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    DimUtils.getWorldID(DimUtils.getDim(placer.world)),
                    1,
                    DateUtils.getDate()
            );
            BlockInfoExtension.enqueue(actionLog);
        }
    }
}
