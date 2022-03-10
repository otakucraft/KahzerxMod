package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.ChunkInfo;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.PalettedContainerInterface;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(PalettedContainer.class)
public abstract class PalettedContainerMixin<T> implements PalettedContainerInterface<T> {
    @Shadow protected abstract PalettedContainer.Data<T> getCompatibleData(PalettedContainer.@Nullable Data<T> previousData, int bits);

    @Shadow @Final private PalettedContainer.PaletteProvider paletteProvider;
    @Shadow @Final private IndexedIterable<T> idList;
    @Shadow private volatile PalettedContainer.Data<T> data;

    @Shadow public abstract int onResize(int i, T object);

    @Shadow public abstract void lock();

    @Shadow public abstract void unlock();

    @Unique
    private T[] presetValues;
    @Unique
    private List<T> paletteEntries;

    @Inject(method = "<init>(Lnet/minecraft/util/collection/IndexedIterable;Lnet/minecraft/world/chunk/PalettedContainer$PaletteProvider;Lnet/minecraft/world/chunk/PalettedContainer$DataProvider;Lnet/minecraft/util/collection/PaletteStorage;Ljava/util/List;)V", at = @At("TAIL"))
    private void prepare(IndexedIterable<T> idList, PalettedContainer.PaletteProvider paletteProvider, PalettedContainer.DataProvider dataProvider, PaletteStorage storage, List<T> paletteEntries, CallbackInfo ci) {
        this.paletteEntries = paletteEntries;
    }

    @Redirect(method = "onResize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/PalettedContainer;getCompatibleData(Lnet/minecraft/world/chunk/PalettedContainer$Data;I)Lnet/minecraft/world/chunk/PalettedContainer$Data;"))
    private PalettedContainer.Data<T> addValues(PalettedContainer instance, PalettedContainer.Data<T> previousData, int bits, int i, T object) {
        if (this.presetValues != null && object != null && previousData.configuration().factory() == PalettedContainer.PaletteProvider.SINGULAR) {
            int duplicates = 0;
            List<T> presetValues = Arrays.asList(this.presetValues);
            duplicates += presetValues.contains(object) ? 1 : 0;
            duplicates += presetValues.contains(previousData.palette().get(0)) ? 1 : 0;
            bits = MathHelper.ceilLog2((1 << paletteProvider.getBits(idList, 1 << bits)) + presetValues.size() - duplicates);
        }
        return this.getCompatibleData(previousData, bits);
    }

    @Redirect(method = "onResize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Palette;index(Ljava/lang/Object;)I"))
    private int addValues(Palette<T> instance, T t) {
        this.addPresetValues();
        return t == null ? -1 : instance.index(t);
    }

    @Override
    public void addValueWithEntry(T[] values) {
        this.presetValues = values;
        PalettedContainer.DataProvider<T> dataProvider = this.data.configuration();
        if (values != null && (dataProvider.factory() == PalettedContainer.PaletteProvider.SINGULAR ? data.palette().get(0) != Blocks.AIR.getDefaultState() : dataProvider.factory() != PaletteDataAccessor.getIdList())) {
            int maxSize = 1 << dataProvider.bits();
            for (T val : values) {
                if (this.data.palette().getSize() >= maxSize) {
                    Set<T> all = new HashSet<>(this.paletteEntries);
                    all.addAll(Arrays.asList(values));
                    int newBytes = MathHelper.ceilLog2(all.size());
                    if (newBytes > dataProvider.bits()) {
                        this.onResize(newBytes, null);
                    }
                    break;
                }
                this.data.palette().index(val);
            }
        }
    }

    @Override
    public void addValue(T[] values) {
        this.presetValues = values;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, ChunkInfo<T> chunkInfo, int yOffset) {
        this.lock();
        try {
            packetByteBuf.writeByte(this.data.storage().getElementBits());
            this.data.palette().writePacket(packetByteBuf);
            if (chunkInfo != null) {
                int chunkSection = (yOffset >> 4) - chunkInfo.getWorldChunk().getBottomSectionCoord();
                chunkInfo.setBits(chunkSection, this.data.configuration().bits());
                chunkInfo.setPalette(chunkSection, this.data.palette());
                chunkInfo.setIndex(chunkSection, packetByteBuf.writerIndex() + PacketByteBuf.getVarIntLength(this.data.storage().getData().length));
            }

            packetByteBuf.writeLongArray(this.data.storage().getData());
            if (chunkInfo != null) {
                int chunkSection = (yOffset >> 4) - chunkInfo.getWorldChunk().getBottomSectionCoord();
                chunkInfo.setPresetValues(chunkSection, this.presetValues);
            }
        } finally {
            this.unlock();
        }
    }

    private void addPresetValues() {
        if (this.presetValues != null && this.data.configuration().factory() != PaletteDataAccessor.getIdList()) {
            for (T presetValue : this.presetValues) {
                this.data.palette().index(presetValue);
            }
        }
    }
}
