package com.kahzerx.kahzerxmod.mixin.kloneExtension;

import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerEntity.class, priority = 69420)
public abstract class PlayerMixin {
    @Redirect(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;velocityModified:Z", ordinal = 0))
    private boolean knockbackKlones(Entity instance) {
        return instance.velocityModified && !(instance instanceof KlonePlayerEntity);
    }
}
