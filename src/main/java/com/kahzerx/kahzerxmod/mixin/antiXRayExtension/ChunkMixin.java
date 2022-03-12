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
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
    @Redirect(method = "fillSectionArray", at = @At(value = "NEW", target = "net/minecraft/world/chunk/ChunkSection"))
    private static ChunkSection addPresetValues(int chunkPos, Registry<Biome> biomeRegistry, HeightLimitView world, Registry<Biome> biome, ChunkSection[] sectionArray) {
        ChunkSection section = new ChunkSection(chunkPos, biomeRegistry);
        ((ChunkSectionInterface) section).addBlockPresets(getWorld(world));
        return section;
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
