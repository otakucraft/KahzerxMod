package com.kahzerx.kahzerxmod.mixin.blockInfoExtension;

import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockActionLog;
import com.kahzerx.kahzerxmod.extensions.blockInfoExtension.BlockInfoExtension;
import com.kahzerx.kahzerxmod.utils.DateUtils;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class BlockBreakingLoggingMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Shadow protected ServerWorld world;

    @Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE))
    private void onBroken(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockActionLog actionLog = new BlockActionLog(
                player.getName().getString(),
                world.getBlockState(pos).getBlock().getTranslationKey(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                DimUtils.getWorldID(DimUtils.getDim(world)),
                0,
                DateUtils.getDate()
        );
        BlockInfoExtension.enqueue(actionLog);
    }
}
