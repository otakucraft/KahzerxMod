package com.kahzerx.kahzerxmod.mixin.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.BlockEntitiesMetrics;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;

@Mixin(World.class)
public abstract class WorldBlockEntityMixin {
    @Shadow @Final protected List<BlockEntityTickInvoker> blockEntityTickers;

    @Shadow public abstract BlockEntity getBlockEntity(BlockPos pos);

    @Shadow public abstract WorldChunk getWorldChunk(BlockPos pos);

    @Inject(method = "tickBlockEntities", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        HashMap<String, HashMap<String, Integer>> worldBlockEntities = new HashMap<>();
        blockEntityTickers.forEach(ticker -> {
            if (ticker.isRemoved()) {
                return;
            }
            if (!getWorldChunk(ticker.getPos()).getLevelType().isAfter(ChunkHolder.LevelType.TICKING)) {
                return;
            }
            BlockEntity be = getBlockEntity(ticker.getPos());
            if (be == null) {
                return;
            }
            Identifier id = BlockEntityType.getId(be.getType());
            if (id == null) {
                return;
            }
            String world = ((World) (Object) this).getRegistryKey().getValue().getPath();
            if (!worldBlockEntities.containsKey(world)) {
                worldBlockEntities.put(world, new HashMap<>());
            }
            worldBlockEntities.get(world).put(
                    id.getPath(),
                    worldBlockEntities.get(world).getOrDefault(id.getPath(), 0) + 1
            );
            BlockEntitiesMetrics.worldBlockEntities = worldBlockEntities;
        });
    }
}
