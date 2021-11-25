package com.kahzerx.kahzerxmod.mixin.blockInfoExtension;

import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockActionLog;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockInfoExtension;
import com.kahzerx.kahzerxmod.utils.DateUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockBreakingLoggingMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    private void onBroken(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (!world.isClient) {
            BlockActionLog actionLog = new BlockActionLog(
                    player.getName().getString(),
                    state.getBlock().getTranslationKey(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    DimUtils.getWorldID(DimUtils.getDim(player.world)),
                    0,
                    DateUtils.getDate()
            );
            BlockInfoExtension.enqueue(actionLog);
        }
    }
}
