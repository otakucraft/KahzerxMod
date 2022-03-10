package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.XRayBlocks;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkPacketInterface;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.*;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;

public class AntiXRay extends ChunkBlockController {
    private static final Palette<BlockState> ID_LIST = new IdListPalette<>(Block.STATE_IDS);
    private static final ChunkSection EMPTY_SECTION = null;
    private static final ThreadLocal<boolean[]> SOLID = ThreadLocal.withInitial(() -> new boolean[Block.STATE_IDS.size()]);
    private static final ThreadLocal<boolean[]> OBFUSCATE = ThreadLocal.withInitial(() -> new boolean[Block.STATE_IDS.size()]);
    // These boolean arrays represent chunk layers, true means don't obfuscate, false means obfuscate
    private static final ThreadLocal<boolean[][]> CURRENT = ThreadLocal.withInitial(() -> new boolean[16][16]);
    private static final ThreadLocal<boolean[][]> NEXT = ThreadLocal.withInitial(() -> new boolean[16][16]);
    private static final ThreadLocal<boolean[][]> NEXT_NEXT = ThreadLocal.withInitial(() -> new boolean[16][16]);
    private final Executor executor;
    private final int maxBlockHeight;
    private final BlockState[] presetBlockStates;
    private final BlockState[] presetBlockStatesFull;
    private final int[] presetBlockStateBitsGlobal;
    private final Object2BooleanOpenHashMap<BlockState> solidGlobal = new Object2BooleanOpenHashMap<>(Block.STATE_IDS.size());
    private final Object2BooleanOpenHashMap<BlockState> obfuscateGlobal = new Object2BooleanOpenHashMap<>(Block.STATE_IDS.size());
    private final ChunkSection[] emptyNearbyChunkSections = {EMPTY_SECTION, EMPTY_SECTION, EMPTY_SECTION, EMPTY_SECTION};
    private final int maxBlockHeightUpdatePosition;
    // Actually these fields should be variables inside the obfuscate method but in sync mode or with SingleThreadExecutor in async mode it's okay (even without ThreadLocal)
    // If an ExecutorService with multiple threads is used, ThreadLocal must be used here
    private final ThreadLocal<int[]> presetBlockStateBits = ThreadLocal.withInitial(() -> new int[getPresetBlockStatesFullLength()]);

    public int getPresetBlockStatesFullLength() {
        return presetBlockStatesFull.length;
    }

    public AntiXRay(World world, Executor executor) {
        this.executor = executor;
        maxBlockHeight = 256 >> 4 << 4;
        List<BlockState> toObfuscate = new ArrayList<>(XRayBlocks.WHITELISTED_BLOCKS);
        toObfuscate.addAll(XRayBlocks.BLACKLISTED_BLOCKS);
        List<BlockState> presetBlockStateList = new LinkedList<>(XRayBlocks.BLACKLISTED_BLOCKS);
        Set<BlockState> presetBlockStateSet = new LinkedHashSet<>(XRayBlocks.BLACKLISTED_BLOCKS);
        presetBlockStates = presetBlockStateSet.toArray(new BlockState[0]);
        presetBlockStatesFull = presetBlockStateList.toArray(new BlockState[0]);
        presetBlockStateBitsGlobal = new int[presetBlockStatesFull.length];
        for (int i = 0; i < presetBlockStatesFull.length; i++) {
            presetBlockStateBitsGlobal[i] = ID_LIST.index(presetBlockStatesFull[i]);
        }
        for (BlockState state : toObfuscate) {
            if (!state.isAir()) {
                obfuscateGlobal.put(state, true);
            }
        }

        EmptyChunk emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0), world.getRegistryManager().get(Registry.BIOME_KEY).entryOf(BiomeKeys.PLAINS));
        BlockPos zPos = new BlockPos(0, 0, 0);

        Block.STATE_IDS.iterator().forEachRemaining(blockState -> {
            solidGlobal.put(blockState, blockState.isSolidBlock(emptyChunk, zPos)
                    && blockState.getBlock() != Blocks.SPAWNER
                    && blockState.getBlock() != Blocks.BARRIER
                    && blockState.getBlock() != Blocks.SHULKER_BOX
                    && blockState.getBlock() != Blocks.SLIME_BLOCK);
        });
        maxBlockHeightUpdatePosition = maxBlockHeight + 1;
    }

    @Override
    public BlockState[] getBlockStates(World world, int bottomBlockY) {
        if (bottomBlockY < maxBlockHeight) {
            return presetBlockStates;
        }
        return null;
    }

    @Override
    public ChunkInfo<BlockState> getChunkInfo(ChunkDataS2CPacket chunkPacket, WorldChunk worldChunk) {
        return new ChunkInfoPacket(chunkPacket, worldChunk, this);
    }

    @Override
    public void modifyBlocks(ChunkDataS2CPacket chunkPacket, ChunkInfo<BlockState> chunkInfo) {
        if (!(chunkInfo instanceof ChunkInfoPacket)) {
            ((ChunkPacketInterface) chunkPacket).setReady(true);
            return;
        }

        if (chunkInfo.getWorldChunk().getWorld() instanceof ServerWorld serverLevel) {
            if (!serverLevel.getServer().isOnThread()) {
                serverLevel.getServer().execute(() -> modifyBlocks(chunkPacket, chunkInfo));
                return;
            }
        }

        WorldChunk chunk = chunkInfo.getWorldChunk();
        int x = chunk.getPos().x;
        int z = chunk.getPos().z;
        ChunkManager source = chunk.getWorld().getChunkManager();
        ((ChunkInfoPacket) chunkInfo).setWorldChunks(
                source.getWorldChunk(x - 1, z, false),
                source.getWorldChunk(x + 1, z, false),
                source.getWorldChunk(x, z - 1, false),
                source.getWorldChunk(x, z + 1, false)
        );
        executor.execute((Runnable) chunkInfo);
    }

    public void obfuscate(ChunkInfoPacket chunkInfoPacket) {
        int[] presetBlockStateBits = this.presetBlockStateBits.get();
        boolean[] solid = SOLID.get();
        boolean[] obfuscate = OBFUSCATE.get();
        boolean[][] current = CURRENT.get();
        boolean[][] next = NEXT.get();
        boolean[][] nextNext = NEXT_NEXT.get();
        BitStorageReader bitStorageReader = new BitStorageReader();
        BitStorageWriter bitStorageWriter = new BitStorageWriter();
        ChunkSection[] nearChunkSections = new ChunkSection[4];
        WorldChunk chunk = chunkInfoPacket.getWorldChunk();
        World world = chunk.getWorld();
        int maxChunkSectionIndex = Math.min((maxBlockHeight >> 4) - chunk.getBottomSectionCoord(), chunk.countVerticalSections() - 1);
        boolean[] solidTemp = null;
        boolean[] obfuscateTemp = null;
        bitStorageReader.setBuffer(chunkInfoPacket.getBuffer());
        bitStorageWriter.setBuffer(chunkInfoPacket.getBuffer());
        int numberOfBlocks = presetBlockStateBits.length;
        IntSupplier random = numberOfBlocks == 1 ? (() -> 0) : new IntSupplier() {
            private int state;
            {
                while ((state = ThreadLocalRandom.current().nextInt()) == 0);
            }

            @Override
            public int getAsInt() {
                state ^= state << 13;
                state ^= state >>> 17;
                state ^= state << 5;
                return (int) ((Integer.toUnsignedLong(state) * numberOfBlocks) >>> 32);
            }
        };
        for (int chunkSectionIndex = 0; chunkSectionIndex <= maxChunkSectionIndex; chunkSectionIndex++) {
            if (chunkInfoPacket.isWritten(chunkSectionIndex) && chunkInfoPacket.getPresetValues(chunkSectionIndex) != null) {
                int[] presetBlockStateBitsTemp;

                if (chunkInfoPacket.getPalette(chunkSectionIndex) instanceof IdListPalette) {
                    presetBlockStateBitsTemp = presetBlockStateBitsGlobal;
                } else {
                    // If it's presetBlockStates, use this.presetBlockStatesFull instead
                    BlockState[] presetBlockStatesFull = chunkInfoPacket.getPresetValues(chunkSectionIndex) == presetBlockStates ? this.presetBlockStatesFull : chunkInfoPacket.getPresetValues(chunkSectionIndex);
                    presetBlockStateBitsTemp = presetBlockStateBits;

                    for (int i = 0; i < presetBlockStateBitsTemp.length; i++) {
                        // This is thread safe because we only request IDs that are guaranteed to be in the palette and are visible
                        // For more details see the comments in the readPalette method
                        presetBlockStateBitsTemp[i] = chunkInfoPacket.getPalette(chunkSectionIndex).index(presetBlockStatesFull[i]);
                    }
                }

                bitStorageWriter.setIndex(chunkInfoPacket.getIndex(chunkSectionIndex));

                // Check if the chunk section below was not obfuscated
                if (chunkSectionIndex == 0 || !chunkInfoPacket.isWritten(chunkSectionIndex - 1) || chunkInfoPacket.getPresetValues(chunkSectionIndex - 1) == null) {
                    // If so, initialize some stuff
                    bitStorageReader.setBits(chunkInfoPacket.getBits(chunkSectionIndex));
                    bitStorageReader.setIndex(chunkInfoPacket.getIndex(chunkSectionIndex));
                    solidTemp = readPalette(chunkInfoPacket.getPalette(chunkSectionIndex), solid, solidGlobal);
                    obfuscateTemp = readPalette(chunkInfoPacket.getPalette(chunkSectionIndex), obfuscate, obfuscateGlobal);
                    // Read the blocks of the upper layer of the chunk section below if it exists
                    ChunkSection belowChunkSection = null;
                    boolean skipFirstLayer = chunkSectionIndex == 0 || (belowChunkSection = chunk.getSectionArray()[chunkSectionIndex - 1]) == EMPTY_SECTION;

                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                            current[z][x] = true;
                            next[z][x] = skipFirstLayer || isTransparent(belowChunkSection, x, 15, z);
                        }
                    }

                    // Abuse the obfuscateLayer method to read the blocks of the first layer of the current chunk section
                    bitStorageWriter.setBits(0);
                    obfuscateLayer(-1, bitStorageReader, bitStorageWriter, solidTemp, obfuscateTemp, presetBlockStateBitsTemp, current, next, nextNext, emptyNearbyChunkSections, random);
                }

                bitStorageWriter.setBits(chunkInfoPacket.getBits(chunkSectionIndex));
                nearChunkSections[0] = chunkInfoPacket.getWorldChunks()[0] == null ? EMPTY_SECTION : chunkInfoPacket.getWorldChunks()[0].getSectionArray()[chunkSectionIndex];
                nearChunkSections[1] = chunkInfoPacket.getWorldChunks()[1] == null ? EMPTY_SECTION : chunkInfoPacket.getWorldChunks()[1].getSectionArray()[chunkSectionIndex];
                nearChunkSections[2] = chunkInfoPacket.getWorldChunks()[2] == null ? EMPTY_SECTION : chunkInfoPacket.getWorldChunks()[2].getSectionArray()[chunkSectionIndex];
                nearChunkSections[3] = chunkInfoPacket.getWorldChunks()[3] == null ? EMPTY_SECTION : chunkInfoPacket.getWorldChunks()[3].getSectionArray()[chunkSectionIndex];

                // Obfuscate all layers of the current chunk section except the upper one
                for (int y = 0; y < 15; y++) {
                    boolean[][] temp = current;
                    current = next;
                    next = nextNext;
                    nextNext = temp;
                    obfuscateLayer(y, bitStorageReader, bitStorageWriter, solidTemp, obfuscateTemp, presetBlockStateBitsTemp, current, next, nextNext, nearChunkSections, random);
                }

                // Check if the chunk section above doesn't need obfuscation
                if (chunkSectionIndex == maxChunkSectionIndex || !chunkInfoPacket.isWritten(chunkSectionIndex + 1) || chunkInfoPacket.getPresetValues(chunkSectionIndex + 1) == null) {
                    // If so, obfuscate the upper layer of the current chunk section by reading blocks of the first layer from the chunk section above if it exists
                    ChunkSection aboveChunkSection;

                    if (chunkSectionIndex != chunk.countVerticalSections() - 1 && (aboveChunkSection = chunk.getSectionArray()[chunkSectionIndex + 1]) != EMPTY_SECTION) {
                        boolean[][] temp = current;
                        current = next;
                        next = nextNext;
                        nextNext = temp;

                        for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                                if (isTransparent(aboveChunkSection, x, 0, z)) {
                                    current[z][x] = true;
                                }
                            }
                        }

                        // There is nothing to read anymore
                        bitStorageReader.setBits(0);
                        solid[0] = true;
                        obfuscateLayer(15, bitStorageReader, bitStorageWriter, solid, obfuscateTemp, presetBlockStateBitsTemp, current, next, nextNext, nearChunkSections, random);
                    }
                } else {
                    // If not, initialize the reader and other stuff for the chunk section above to obfuscate the upper layer of the current chunk section
                    bitStorageReader.setBits(chunkInfoPacket.getBits(chunkSectionIndex + 1));
                    bitStorageReader.setIndex(chunkInfoPacket.getIndex(chunkSectionIndex + 1));
                    solidTemp = readPalette(chunkInfoPacket.getPalette(chunkSectionIndex + 1), solid, solidGlobal);
                    obfuscateTemp = readPalette(chunkInfoPacket.getPalette(chunkSectionIndex + 1), obfuscate, obfuscateGlobal);
                    boolean[][] temp = current;
                    current = next;
                    next = nextNext;
                    nextNext = temp;
                    obfuscateLayer(15, bitStorageReader, bitStorageWriter, solidTemp, obfuscateTemp, presetBlockStateBitsTemp, current, next, nextNext, nearChunkSections, random);
                }
                bitStorageWriter.flush();
            }
        }
        ((ChunkPacketInterface) chunkInfoPacket.getChunkPacket()).setReady(true);
    }

    private void obfuscateLayer(int y, BitStorageReader bitStorageReader, BitStorageWriter bitStorageWriter, boolean[] solid, boolean[] obfuscate, int[] presetBlockStateBits, boolean[][] current, boolean[][] next, boolean[][] nextNext, ChunkSection[] nearbyChunkSections, IntSupplier random) {
        // First block of first line
        int bits = bitStorageReader.read();

        if (nextNext[0][0] = !solid[bits]) {
            bitStorageWriter.skip();
            next[0][1] = true;
            next[1][0] = true;
        } else {
            if (current[0][0] || isTransparent(nearbyChunkSections[2], 0, y, 15) || isTransparent(nearbyChunkSections[0], 15, y, 0)) {
                bitStorageWriter.skip();
            } else {
                bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
            }
        }

        if (!obfuscate[bits]) {
            next[0][0] = true;
        }

        // First line
        for (int x = 1; x < 15; x++) {
            bits = bitStorageReader.read();

            if (nextNext[0][x] = !solid[bits]) {
                bitStorageWriter.skip();
                next[0][x - 1] = true;
                next[0][x + 1] = true;
                next[1][x] = true;
            } else {
                if (current[0][x] || isTransparent(nearbyChunkSections[2], x, y, 15)) {
                    bitStorageWriter.skip();
                } else {
                    bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
                }
            }

            if (!obfuscate[bits]) {
                next[0][x] = true;
            }
        }

        // Last block of first line
        bits = bitStorageReader.read();

        if (nextNext[0][15] = !solid[bits]) {
            bitStorageWriter.skip();
            next[0][14] = true;
            next[1][15] = true;
        } else {
            if (current[0][15] || isTransparent(nearbyChunkSections[2], 15, y, 15) || isTransparent(nearbyChunkSections[1], 0, y, 0)) {
                bitStorageWriter.skip();
            } else {
                bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
            }
        }

        if (!obfuscate[bits]) {
            next[0][15] = true;
        }

        // All inner lines
        for (int z = 1; z < 15; z++) {
            // First block
            bits = bitStorageReader.read();

            if (nextNext[z][0] = !solid[bits]) {
                bitStorageWriter.skip();
                next[z][1] = true;
                next[z - 1][0] = true;
                next[z + 1][0] = true;
            } else {
                if (current[z][0] || isTransparent(nearbyChunkSections[0], 15, y, z)) {
                    bitStorageWriter.skip();
                } else {
                    bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
                }
            }

            if (!obfuscate[bits]) {
                next[z][0] = true;
            }

            // All inner blocks
            for (int x = 1; x < 15; x++) {
                bits = bitStorageReader.read();

                if (nextNext[z][x] = !solid[bits]) {
                    bitStorageWriter.skip();
                    next[z][x - 1] = true;
                    next[z][x + 1] = true;
                    next[z - 1][x] = true;
                    next[z + 1][x] = true;
                } else {
                    if (current[z][x]) {
                        bitStorageWriter.skip();
                    } else {
                        bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
                    }
                }

                if (!obfuscate[bits]) {
                    next[z][x] = true;
                }
            }

            // Last block
            bits = bitStorageReader.read();

            if (nextNext[z][15] = !solid[bits]) {
                bitStorageWriter.skip();
                next[z][14] = true;
                next[z - 1][15] = true;
                next[z + 1][15] = true;
            } else {
                if (current[z][15] || isTransparent(nearbyChunkSections[1], 0, y, z)) {
                    bitStorageWriter.skip();
                } else {
                    bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
                }
            }

            if (!obfuscate[bits]) {
                next[z][15] = true;
            }
        }

        // First block of last line
        bits = bitStorageReader.read();

        if (nextNext[15][0] = !solid[bits]) {
            bitStorageWriter.skip();
            next[15][1] = true;
            next[14][0] = true;
        } else {
            if (current[15][0] || isTransparent(nearbyChunkSections[3], 0, y, 0) || isTransparent(nearbyChunkSections[0], 15, y, 15)) {
                bitStorageWriter.skip();
            } else {
                bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
            }
        }

        if (!obfuscate[bits]) {
            next[15][0] = true;
        }

        // Last line
        for (int x = 1; x < 15; x++) {
            bits = bitStorageReader.read();

            if (nextNext[15][x] = !solid[bits]) {
                bitStorageWriter.skip();
                next[15][x - 1] = true;
                next[15][x + 1] = true;
                next[14][x] = true;
            } else {
                if (current[15][x] || isTransparent(nearbyChunkSections[3], x, y, 0)) {
                    bitStorageWriter.skip();
                } else {
                    bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
                }
            }

            if (!obfuscate[bits]) {
                next[15][x] = true;
            }
        }

        // Last block of last line
        bits = bitStorageReader.read();

        if (nextNext[15][15] = !solid[bits]) {
            bitStorageWriter.skip();
            next[15][14] = true;
            next[14][15] = true;
        } else {
            if (current[15][15] || isTransparent(nearbyChunkSections[3], 15, y, 0) || isTransparent(nearbyChunkSections[1], 0, y, 15)) {
                bitStorageWriter.skip();
            } else {
                bitStorageWriter.write(presetBlockStateBits[random.getAsInt()]);
            }
        }

        if (!obfuscate[bits]) {
            next[15][15] = true;
        }
    }

    private boolean isTransparent(ChunkSection chunkSection, int x, int y, int z) {
        if (chunkSection == EMPTY_SECTION) {
            return true;
        }
        try {
            return !solidGlobal.getOrDefault(chunkSection.getBlockState(x, y, z), false);
        } catch (EntryMissingException e) {
            return true;
        }
    }

    private boolean[] readPalette(Palette<BlockState> palette, boolean[] temp, Object2BooleanOpenHashMap<BlockState> global) {
        try {
            for (int i = 0; i < palette.getSize(); i++) {
                temp[i] = global.getOrDefault(palette.get(i), false);
            }
        } catch (EntryMissingException ignored) { }
        return temp;
    }

    @Override
    public void onBlockChange(World level, BlockPos blockPos, BlockState newBlockState, BlockState oldBlockState, int flags, int maxUpdateDepth) {
        if (oldBlockState != null && solidGlobal.getOrDefault(oldBlockState, false) && !solidGlobal.getOrDefault(newBlockState, false) && blockPos.getY() <= maxBlockHeightUpdatePosition) {
            updateNearbyBlocks(level, blockPos);
        }
    }

    @Override
    public void onPlayerLeftClickBlock(World world, ServerPlayerInteractionManager serverPlayerInteractionManager, BlockPos blockPos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        if (blockPos.getY() <= maxBlockHeightUpdatePosition) {
            updateNearbyBlocks(world, blockPos);
        }
    }

    private void updateNearbyBlocks(World world, BlockPos blockPos) {
        BlockPos temp = blockPos.west();
        updateBlock(world, temp);
        updateBlock(world, temp.west());
        updateBlock(world, temp.down());
        updateBlock(world, temp.up());
        updateBlock(world, temp.north());
        updateBlock(world, temp.south());
        updateBlock(world, temp = blockPos.east());
        updateBlock(world, temp.east());
        updateBlock(world, temp.down());
        updateBlock(world, temp.up());
        updateBlock(world, temp.north());
        updateBlock(world, temp.south());
        updateBlock(world, temp = blockPos.down());
        updateBlock(world, temp.down());
        updateBlock(world, temp.north());
        updateBlock(world, temp.south());
        updateBlock(world, temp = blockPos.up());
        updateBlock(world, temp.up());
        updateBlock(world, temp.north());
        updateBlock(world, temp.south());
        updateBlock(world, temp = blockPos.north());
        updateBlock(world, temp.north());
        updateBlock(world, temp = blockPos.south());
        updateBlock(world, temp.south());
    }

    private void updateBlock(World world, BlockPos pos) {
        WorldChunk chunk = world.getChunkManager().getWorldChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
        if (chunk != null && obfuscateGlobal.getOrDefault(chunk.getBlockState(pos), false)) {
            ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        }
    }
}
