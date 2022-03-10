package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkBlockController;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkSectionInterface;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.PalettedContainerInterface;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.WorldInterface;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkSection.class)
public abstract class ChunkSectionMixin implements ChunkSectionInterface {
    @Shadow @Final private int yOffset;

    @Shadow @Final private PalettedContainer<BlockState> blockStateContainer;

    @Shadow private short nonEmptyBlockCount;

    @Shadow @Final private PalettedContainer<RegistryEntry<Biome>> biomeContainer;

    @Override
    @SuppressWarnings("unchecked")
    public void addBlockPresets(World world) {
        BlockState[] blockStates = null;
        if (world instanceof WorldInterface worldInterface) {
            ChunkBlockController controller = worldInterface.getChunkBlockController();
            if (controller != null) {
                blockStates = controller.getBlockStates(world, this.yOffset);
            }
        }
        ((PalettedContainerInterface<BlockState>) this.blockStateContainer).addValue(blockStates);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(PacketByteBuf packetByteBuf, ChunkInfo<BlockState> chunkInfo) {
        packetByteBuf.writeShort(this.nonEmptyBlockCount);
        ((PalettedContainerInterface<BlockState>) this.blockStateContainer).write(packetByteBuf, chunkInfo, this.yOffset);
        this.biomeContainer.writePacket(packetByteBuf);
    }
}
