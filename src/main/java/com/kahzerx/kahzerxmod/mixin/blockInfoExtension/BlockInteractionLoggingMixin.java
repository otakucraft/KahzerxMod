package com.kahzerx.kahzerxmod.mixin.blockInfoExtension;

import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers.ActionTypes;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.helpers.BlockActionLog;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockInfoExtension;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.utils.BlockInfoUtils;
import com.kahzerx.kahzerxmod.utils.DateUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class BlockInteractionLoggingMixin {
    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;shouldCancelInteraction()Z"))
    private void onRightClick(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (BlockInfoUtils.shouldRegisterBlock(world.getBlockState(hitResult.getBlockPos()).getBlock(), player)) {
            BlockActionLog actionLog = new BlockActionLog(
                    player.getName().getString(),
                    1,
                    world.getBlockState(hitResult.getBlockPos()).getBlock().getName().getString(),
                    hitResult.getBlockPos().getX(),
                    hitResult.getBlockPos().getY(),
                    hitResult.getBlockPos().getZ(),
                    DimUtils.getWorldID(DimUtils.getDim(player.world)),
                    ActionTypes.INTERACT.getId(),
                    DateUtils.getDate()
            );
            BlockInfoExtension.enqueue(actionLog);
        } else if (BlockInfoUtils.shouldRegisterItem(player, stack)) {
            BlockActionLog actionLog = new BlockActionLog(
                    player.getName().getString(),
                    1,
                    stack.getItem().getName().getString(),
                    hitResult.getBlockPos().getX(),
                    hitResult.getBlockPos().getY(),
                    hitResult.getBlockPos().getZ(),
                    DimUtils.getWorldID(DimUtils.getDim(player.world)),
                    ActionTypes.INTERACT.getId(),
                    DateUtils.getDate()
            );
            BlockInfoExtension.enqueue(actionLog);
        }
    }
}
