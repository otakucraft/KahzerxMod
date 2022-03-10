package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkSectionInterface;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
    @Inject(method = "fillSectionArray", at = @At("TAIL"))
    private static void addPresetValues(HeightLimitView world, Registry<Biome> biome, ChunkSection[] sectionArray, CallbackInfo ci) {
        for (ChunkSection section : sectionArray) {
            ((ChunkSectionInterface) section).addBlockPresets(getWorld(world));
        }
    }

    private static World getWorld(HeightLimitView heightLimitView) {
        if (heightLimitView instanceof World world) {
            return world;
        } else if (heightLimitView instanceof WorldChunk worldChunk) {
            return worldChunk.getWorld();
        } else if (heightLimitView instanceof Chunk chunk) {
            return getWorld(chunk.getHeightLimitView());
        } else {
            throw new IllegalStateException("Failed to add block presets as height accessor was an instance of" + heightLimitView.getClass().getSimpleName());
        }
    }
}
