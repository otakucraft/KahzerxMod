package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.BlockEntityInstance;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockEntitiesProfiler extends AbstractProfiler {
    private static final ArrayList<BlockEntityInstance> blockEntityList = new ArrayList<>();

    @Override
    public void onTick(MinecraftServer server, String id) {
        blockEntityList.add(new BlockEntityInstance("", "", 0.0D, 0.0D, 0.0D));
        this.addResult(server.getTicks(), new ProfilerResult("block_entities", id, new ArrayList<>(blockEntityList)));
        blockEntityList.clear();
    }

    public static void addBlockEntity(BlockEntity be, World world) {
        Identifier id = BlockEntityType.getId(be.getType());
        if (id == null) {
            return;
        }
        BlockPos pos = be.getPos();
        blockEntityList.add(new BlockEntityInstance(id.getPath(), world.getRegistryKey().getValue().getPath(), pos.getX(), pos.getY(), pos.getZ()));
    }
}
