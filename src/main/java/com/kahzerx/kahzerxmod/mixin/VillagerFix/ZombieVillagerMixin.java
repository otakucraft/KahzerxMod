package com.kahzerx.kahzerxmod.mixin.VillagerFix;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ZombieVillagerEntity.class)
public class ZombieVillagerMixin {
    @Redirect(method = "finishConversion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;setExperience(I)V"))
    private void fixVillagerBrain(VillagerEntity instance, int experience, ServerWorld world) {
        instance.reinitializeBrain(world);
        instance.setExperience(experience);
    }
}
