package com.kahzerx.kahzerxmod.mixin.villagersFollowEmeraldExtension;

import com.kahzerx.kahzerxmod.extensions.villagersFollowEmeraldExtension.VillagersFollowEmeraldExtension;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    private PlayerEntity player = null;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (VillagersFollowEmeraldExtension.isExtensionEnabled && !this.isAiDisabled()) {
            if (world.getTime() % 60 == 0) {
                player = world.getClosestPlayer(
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        10,
                        (player) ->
                                player instanceof PlayerEntity
                                        && !player.isSpectator()
                                        && ((PlayerEntity) player).isHolding(Items.EMERALD_BLOCK));
            }
            if (player != null) {
                getLookControl().lookAt(player, getMaxHeadRotation(), getMaxLookPitchChange());
                if (squaredDistanceTo(player) < 6.25D) {
                    getNavigation().stop();
                } else {
                    getNavigation().startMovingTo(player, 0.6);
                }
            }
        }
    }
}
