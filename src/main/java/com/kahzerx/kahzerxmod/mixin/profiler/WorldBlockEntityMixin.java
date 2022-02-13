package com.kahzerx.kahzerxmod.mixin.profiler;

import com.kahzerx.kahzerxmod.profiler.BlockEntitiesProfiler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class WorldBlockEntityMixin {
    @Shadow @Nullable public abstract BlockEntity getBlockEntity(BlockPos pos);

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V"))
    private void onTick(BlockEntityTickInvoker instance) {
        instance.tick();
        BlockEntity be = getBlockEntity(instance.getPos());
        if (be == null) {
            return;
        }

        BlockEntitiesProfiler.addBlockEntity(be, (World) (Object) this);
    }
}
