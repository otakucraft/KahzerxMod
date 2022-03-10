package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkInfo<T> {
    private final ChunkDataS2CPacket chunkPacket;
    private final WorldChunk worldChunk;
    private final int[] bits;
    private final Object[] palettes;
    private final int[] indexes;
    private final Object[][] values;
    private byte[] buffer;

    public ChunkInfo(ChunkDataS2CPacket chunkPacket, WorldChunk worldChunk) {
        this.chunkPacket = chunkPacket;
        this.worldChunk = worldChunk;
        int sections = worldChunk.countVerticalSections();
        this.bits = new int[sections];
        this.palettes = new Object[sections];
        this.indexes = new int[sections];
        this.values = new Object[sections][];
    }

    public ChunkDataS2CPacket getChunkPacket() {
        return chunkPacket;
    }

    public WorldChunk getWorldChunk() {
        return worldChunk;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getBits(int chunkSectionIndex) {
        return bits[chunkSectionIndex];
    }

    public void setBits(int chunkSectionIndex, int bits) {
        this.bits[chunkSectionIndex] = bits;
    }

    public Palette<T> getPalette(int chunkSectionIndex) {
        return (Palette<T>) palettes[chunkSectionIndex];
    }

    public void setPalette(int chunkSectionIndex, Palette<T> palette) {
        palettes[chunkSectionIndex] = palette;
    }

    public int getIndex(int chunkSectionIndex) {
        return indexes[chunkSectionIndex];
    }

    public void setIndex(int chunkSectionIndex, int index) {
        indexes[chunkSectionIndex] = index;
    }

    public T[] getPresetValues(int chunkSectionIndex) {
        return (T[]) values[chunkSectionIndex];
    }

    public void setPresetValues(int chunkSectionIndex, T[] presetValues) {
        this.values[chunkSectionIndex] = presetValues;
    }

    public boolean isWritten(int chunkSectionIndex) {
        return bits[chunkSectionIndex] != 0;
    }
}
