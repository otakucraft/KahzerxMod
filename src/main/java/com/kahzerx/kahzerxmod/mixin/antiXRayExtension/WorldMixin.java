package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.AntiXRay;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.WorldInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.Executor;

@Mixin(World.class)
public abstract class WorldMixin implements WorldInterface, WorldAccess {
    @Unique
    public ChunkBlockController chunkBlockController;

    @Override
    public void initValues(Executor executor) {
        this.chunkBlockController = new AntiXRay((World) (Object) this, executor);
    }

    @Override
    public ChunkBlockController getChunkBlockController() {
        return chunkBlockController;
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onBlockChange(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir, WorldChunk worldChunk, Block block) {
        BlockState oldState = worldChunk.getBlockState(pos);
        this.chunkBlockController.onBlockChange((World) (Object) this, pos, state, oldState, flags, maxUpdateDepth);
    }
}
