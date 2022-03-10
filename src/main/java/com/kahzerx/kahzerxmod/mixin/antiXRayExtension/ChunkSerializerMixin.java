package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.PalettedContainerInterface;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ChunkSerializer.class, priority = 1500)
public class ChunkSerializerMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;initForPalette(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/ChunkSection;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addValues(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos2, UpgradeData upgradeData, boolean bl, NbtList nbtList, int i, ChunkSection[] chunkSections, boolean bl2, ChunkManager chunkManager, LightingProvider lightingProvider, Registry registry, Codec codec, int j, NbtCompound nbtCompound, int k, int l, PalettedContainer palettedContainer, PalettedContainer palettedContainer2, ChunkSection chunkSection) {
        PalettedContainerInterface<BlockState> container = (PalettedContainerInterface<BlockState>) palettedContainer;
        BlockState[] values = ChunkBlockController.getBlockController(world).getBlockStates(world, k << 4);
        if (nbtCompound.contains("block_states", 10)) {
            container.addValueWithEntry(values);
        } else {
            container.addValue(values);
        }
    }
//ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir, MapState.UpdateData updateData, boolean bl, NbtList nbtList, int i, ChunkSection[] chunkSections, boolean bl2, ChunkManager chunkManager, LightingProvider lightingProvider, Registry<Biome> registry, Codec<PalettedContainer<RegistryEntry<Biome>>> codec, int j, NbtCompound nbtCompound, int k, int l, PalettedContainer palettedContainer, PalettedContainer palettedContainer2

}
