package com.kahzerx.kahzerxmod.mixin.kloneExtension;

import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlMixin extends ThrownEntity {
    protected EnderPearlMixin(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isOpen()Z"))
    private boolean hasGoodConnection(ClientConnection instance) {
        return instance.isOpen() || this.getOwner() instanceof KlonePlayerEntity;
    }
}
