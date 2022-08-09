package com.kahzerx.kahzerxmod.mixin.noReports;

import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "verify", at = @At(value = "RETURN"), cancellable = true)
    public void onVerify(SignedMessage message, MessageSourceProfile profile, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
