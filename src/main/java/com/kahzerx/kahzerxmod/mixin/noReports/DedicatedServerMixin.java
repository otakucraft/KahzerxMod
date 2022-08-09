package com.kahzerx.kahzerxmod.mixin.noReports;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "shouldEnforceSecureProfile", at = @At("HEAD"), cancellable = true)
    private void onSecure(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
