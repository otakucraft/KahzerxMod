package com.kahzerx.kahzerxmod.mixin.server;

import com.kahzerx.kahzerxmod.KahzerxServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ChunkEventsMixin {
    @Shadow @Final ServerWorld world;

    @Inject(method = "method_17227", at = @At("TAIL"))
    private void onChunkLoad(ChunkHolder chunkHolder, Chunk protoChunk, CallbackInfoReturnable<Chunk> cir) {
        KahzerxServer.onChunkLoad(this.world, (WorldChunk) cir.getReturnValue());
    }

    @Inject(method = "method_18843", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setLoadedToWorld(Z)V", shift = At.Shift.AFTER))
    private void onChunkUnload(ChunkHolder chunkHolder, CompletableFuture completableFuture, long l, Chunk chunk, CallbackInfo ci) {
        KahzerxServer.onChunkUnload(this.world, (WorldChunk) chunk);
    }
}
