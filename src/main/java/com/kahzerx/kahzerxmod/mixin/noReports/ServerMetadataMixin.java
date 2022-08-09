package com.kahzerx.kahzerxmod.mixin.noReports;

import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerMetadata.class)
public class ServerMetadataMixin {
    @Inject(method = "isSecureChatEnforced", at = @At(value = "RETURN"), cancellable = true)
    private void onSecure(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
