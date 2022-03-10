package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkDataInterface;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkPacketInterface;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ChunkDataS2CPacket.class)
public abstract class ChunkDataPacketMixin implements ChunkPacketInterface {
    @Shadow @Final private ChunkData chunkData;
    @Unique
    private volatile boolean ready = false;

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/light/LightingProvider;Ljava/util/BitSet;Ljava/util/BitSet;Z)V", at = @At("TAIL"))
    private void onInit(WorldChunk chunk, LightingProvider lightProvider, BitSet skyBits, BitSet blockBits, boolean nonEdge, CallbackInfo ci) {
        ChunkDataS2CPacket packet = (ChunkDataS2CPacket) (Object) this;
        ChunkBlockController controller = ChunkBlockController.getBlockController(chunk.getWorld());
        ChunkInfo<BlockState> chunkInfo = controller.getChunkInfo(packet, chunk);
        ((ChunkDataInterface) this.chunkData).customChunkData(chunkInfo);
        controller.modifyBlocks(packet, chunkInfo);
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
