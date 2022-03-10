package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import net.minecraft.network.PacketByteBuf;

public interface PalettedContainerInterface<T> {
    void addValueWithEntry(T[] values);
    void addValue(T[] values);
    void write(PacketByteBuf packetByteBuf, ChunkInfo<T> chunkInfo, int yOffset);
}
