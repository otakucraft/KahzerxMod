package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkPacketInterface;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.WorldInterface;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkBlockController {
    public static final ChunkBlockController NO_OPERATION = new ChunkBlockController();

    protected ChunkBlockController() { }

    public BlockState[] getBlockStates(World world, int bottomBlockY) {
        return null;
    }

    public ChunkInfo<BlockState> getChunkInfo(ChunkDataS2CPacket chunkPacket, WorldChunk worldChunk) {
        return null;
    }

    public void modifyBlocks(ChunkDataS2CPacket chunkPacket, ChunkInfo<BlockState> chunkInfo) {
        ((ChunkPacketInterface) chunkPacket).setReady(true);
    }

    public void onBlockChange(World level, BlockPos blockPos, BlockState newBlockState, BlockState oldBlockState, int flags, int maxUpdateDepth) { }

    public void onPlayerLeftClickBlock(World world, ServerPlayerInteractionManager serverPlayerInteractionManager, BlockPos blockPos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) { }

    public static ChunkBlockController getBlockController(World level) {
        return ((WorldInterface) level).getChunkBlockController();
    }
}
