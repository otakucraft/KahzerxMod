package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PalettedContainer.PaletteProvider.class)
public interface PaletteDataAccessor {
    @Accessor("ID_LIST")
    static Palette.Factory getIdList() { return null; }
}
