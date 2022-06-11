package com.kahzerx.kahzerxmod.mixin.farmlandMyceliumExtension;

import com.kahzerx.kahzerxmod.extensions.farmlandMyceliumExtension.FarmlandMyceliumExtension;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public class HoeItemMixin {
    private boolean replaced = false;

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"))
    private void onUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        Block b = world.getBlockState(context.getBlockPos()).getBlock();
        if (b == Blocks.MYCELIUM && FarmlandMyceliumExtension.isExtensionEnabled) {
            world.setBlockState(context.getBlockPos(), Blocks.GRASS_BLOCK.getDefaultState());
            world.updateNeighbors(context.getBlockPos(), Blocks.GRASS_BLOCK);
            replaced = true;
        }
    }

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;success(Z)Lnet/minecraft/util/ActionResult;"))
    private ActionResult onSuccess(boolean swingHand, ItemUsageContext context) {
        World world = context.getWorld();
        if (replaced) {
            replaced = false;
            return ActionResult.success(true);
        }
        return ActionResult.success(world.isClient);
    }
}
