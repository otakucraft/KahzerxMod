package com.kahzerx.kahzerxmod.mixin.kloneExtension;

import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow @Nullable public abstract Entity getPrimaryPassenger();

    @Shadow public World world;

    @Inject(method = "isLogicalSideForUpdatingMovement", at = @At("HEAD"), cancellable = true)
    private void isKlone(CallbackInfoReturnable<Boolean> cir) {
        if (this.getPrimaryPassenger() instanceof KlonePlayerEntity) {
            cir.setReturnValue(!world.isClient());
        }
    }
}
