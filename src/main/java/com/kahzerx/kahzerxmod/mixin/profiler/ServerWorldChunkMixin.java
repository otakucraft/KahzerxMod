package com.kahzerx.kahzerxmod.mixin.profiler;

import com.kahzerx.kahzerxmod.profiler.ChunkProfiler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldChunkMixin {
    @Inject(method = "tickChunk", at = @At(value = "HEAD"))
    private void onTick(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ChunkProfiler.addChunk(chunk);
    }
}
