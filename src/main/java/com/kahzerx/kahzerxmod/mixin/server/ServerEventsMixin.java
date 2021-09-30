package com.kahzerx.kahzerxmod.mixin.server;

import com.kahzerx.kahzerxmod.KahzerxServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerEventsMixin {
    @Inject(method = "runServer", at = @At("HEAD"))
    private void onRun(CallbackInfo ci) {
        KahzerxServer.onRunServer((MinecraftServer) (Object) this);
        KahzerxServer.onCreateDatabase();
    }

    @Inject(method = "runServer", at = @At("RETURN"))
    private void onStop(CallbackInfo ci) {
        KahzerxServer.onStopServer();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;saveAllPlayerData()V"))
    private void onSave(CallbackInfo ci) {
        KahzerxServer.onAutoSave();
    }
}
