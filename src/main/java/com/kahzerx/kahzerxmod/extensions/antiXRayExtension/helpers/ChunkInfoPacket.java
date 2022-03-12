package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkInfoPacket extends ChunkInfo<BlockState> implements Runnable {
    private final AntiXRay antiXRay;
    private WorldChunk[] worldChunks;
    public ChunkInfoPacket(ChunkDataS2CPacket chunkPacket, WorldChunk worldChunk, AntiXRay antiXRay) {
        super(chunkPacket, worldChunk);
        this.antiXRay = antiXRay;
    }

    public WorldChunk[] getWorldChunks() {
        return worldChunks;
    }

    public void setWorldChunks(WorldChunk... worldChunks) {
        this.worldChunks = worldChunks;
    }

    @Override
    public void run() {
        antiXRay.obfuscate(this);
    }
}
