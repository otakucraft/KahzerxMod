package com.kahzerx.kahzerxmod.mixin.deepslateInstaMineExtension;

import com.kahzerx.kahzerxmod.extensions.deepslateInstaMineExtension.DeepslateInstaMineExtension;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractStateMixin {
    @Shadow public abstract Block getBlock();
    @Inject(method = "getHardness", at = @At("HEAD"), cancellable = true)
    public void onGetHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (DeepslateInstaMineExtension.isExtensionEnabled && (this.getBlock() == Blocks.DEEPSLATE || this.getBlock() == Blocks.COBBLED_DEEPSLATE)) {
            cir.setReturnValue(1.5F);
        }
    }
}
