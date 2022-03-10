package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkDataInterface;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkSectionInterface;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkData.class)
public abstract class ChunkDataMixin implements ChunkDataInterface {
    @Shadow @Final private byte[] sectionsData;
    @Unique
    private PacketByteBuf buf;
    @Unique
    private WorldChunk chunk;

    @Redirect(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ChunkData;writeSections(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/world/chunk/WorldChunk;)V"))
    private void prepareValues(PacketByteBuf buf, WorldChunk chunk) {
        this.buf = buf;
        this.chunk = chunk;
    }

    @Override
    public void customChunkData(ChunkInfo<BlockState> chunkInfo) {
        if (chunkInfo != null) {
            chunkInfo.setBuffer(sectionsData);
        }

        for (ChunkSection section : this.chunk.getSectionArray()) {
            ((ChunkSectionInterface) section).write(this.buf, chunkInfo);
        }
    }
}
