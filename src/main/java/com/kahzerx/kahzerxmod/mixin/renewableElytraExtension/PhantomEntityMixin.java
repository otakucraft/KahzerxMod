package com.kahzerx.kahzerxmod.mixin.renewableElytraExtension;

import com.kahzerx.kahzerxmod.extensions.renewableElytraExtension.RenewableElytraExtension;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(PhantomEntity.class)
public class PhantomEntityMixin extends FlyingEntity {
    protected PhantomEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (RenewableElytraExtension.isExtensionEnabled && source.getAttacker() instanceof ShulkerEntity && new Random().nextDouble() < 0.25D) {
            this.dropStack(new ItemStack(Items.ELYTRA));
        }
    }
}
