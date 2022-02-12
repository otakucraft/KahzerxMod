package com.kahzerx.kahzerxmod.mixin.solExtension;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1))
    private boolean collide(LivingEntity instance, DamageSource source, float amount) {
        FireworkRocketEntity f1 = (FireworkRocketEntity) source.getSource();
        if (f1 != null) {
            ItemStack rocketItem = f1.getStack();
            NbtCompound nbt = rocketItem.getNbt();
            if (nbt != null && nbt.getBoolean("Sol")) {
                return false;
            }
        }
        return instance.damage(source, amount);
    }
}
