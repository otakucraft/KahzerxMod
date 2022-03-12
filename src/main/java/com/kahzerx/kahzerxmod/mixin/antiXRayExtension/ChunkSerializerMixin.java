package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.PalettedContainerInterface;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.tick.SimpleTickScheduler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkSerializer.class, priority = 1500)
public abstract class ChunkSerializerMixin {
    private static final Codec<PalettedContainer<BlockState>> CODEC = PalettedContainer.createCodec(Block.STATE_IDS, BlockState.CODEC, PalettedContainer.PaletteProvider.BLOCK_STATE, Blocks.AIR.getDefaultState());
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unchecked")
    @Inject(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtList;size()I", ordinal = 0))
    private static void onSize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        NbtList nbtList = nbt.getList("sections", 10);
        int i = world.countVerticalSections();
        ChunkSection[] chunkSections = new ChunkSection[i];
        for (int j = 0; j < nbtList.size(); ++j) {
            NbtCompound nbtCompound = nbtList.getCompound(j);
            byte k = nbtCompound.getByte("Y");
            int l = world.sectionCoordToIndex(k);
            if (l >= 0 && l < chunkSections.length) {
                PalettedContainer palettedContainer;
                if (nbtCompound.contains("block_states", 10)) {
                    palettedContainer = CODEC.parse(NbtOps.INSTANCE, nbtCompound.getCompound("block_states")).promotePartial(errorMessage -> logRecoverableError(chunkPos, k, errorMessage)).getOrThrow(false, LOGGER::error);
                } else {
                    palettedContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
                }
                PalettedContainerInterface<BlockState> container = (PalettedContainerInterface<BlockState>) palettedContainer;
                BlockState[] values = ChunkBlockController.getBlockController(world).getBlockStates(world, k << 4);
                if (nbtCompound.contains("block_states", 10)) {
                    container.addValueWithEntry(values);
                } else {
                    container.addValue(values);
                }
            }
        }
    }

    private static void logRecoverableError(ChunkPos chunkPos, int y, String message) {
        LOGGER.error("Recoverable errors when loading section [" + chunkPos.x + ", " + y + ", " + chunkPos.z + "]: " + message);
    }
}
