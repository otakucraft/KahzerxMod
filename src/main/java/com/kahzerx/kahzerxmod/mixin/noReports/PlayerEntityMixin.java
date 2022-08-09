package com.kahzerx.kahzerxmod.mixin.noReports;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getPublicKey", at = @At(value = "RETURN"), cancellable = true)
    private void onGet(CallbackInfoReturnable<PlayerPublicKey> cir) {
        cir.setReturnValue(null);
    }
}
