package com.kahzerx.kahzerxmod.mixin.shopExtension;


import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private static final Logger LOGGER = LogManager.getLogger();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "triggerItemPickedUpByEntityCriteria", at = @At(value = "HEAD"))
    private void onPickUp(ItemEntity item, CallbackInfo ci) {
        if (ShopExtension.isExtensionEnabled) {
            LOGGER.info(String.format("%s picked up %s @ %d %d %d in %s", this.getName().getString(), item.getName().getString(), item.getBlockPos().getX(), item.getBlockPos().getY(), item.getBlockPos().getZ(), item.getWorld().getRegistryKey().getValue().getPath()));
        }
    }
}
