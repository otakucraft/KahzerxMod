package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public interface ChunkSectionInterface {
    void addBlockPresets(World world);
    void write(PacketByteBuf packetByteBuf, ChunkInfo<BlockState> chunkInfo);
}
