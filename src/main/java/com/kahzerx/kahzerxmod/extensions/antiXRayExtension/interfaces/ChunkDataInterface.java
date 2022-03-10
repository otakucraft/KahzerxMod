package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import net.minecraft.block.BlockState;

public interface ChunkDataInterface {
    void customChunkData(ChunkInfo<BlockState> chunkInfo);
}
