package com.kahzerx.kahzerxmod.mixin.maintenanceExtension;

import com.kahzerx.kahzerxmod.extensions.maintenanceExtension.MaintenanceExtension;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow public abstract boolean isOperator(GameProfile profile);

    @Inject(method = "checkCanJoin", at = @At("RETURN"), cancellable = true)
    public void isMaintenance(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        if (MaintenanceExtension.isExtensionEnabled && !isOperator(profile)) {
            cir.setReturnValue(new LiteralText("Server is closed for maintenance"));
        }
    }
}
