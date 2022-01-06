package com.kahzerx.kahzerxmod.mixin.kloneExtension;

import com.kahzerx.kahzerxmod.klone.KloneNetworkHandler;
import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = PlayerManager.class, priority = 5000)
public abstract class PlayerManagerMixin {
    @Shadow public abstract List<ServerPlayerEntity> getPlayerList();

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "createPlayer", at = @At("HEAD"))
    private void onCreatePlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        for (ServerPlayerEntity player : getPlayerList()) {
            if (player.getGameProfile().getId().equals(profile.getId()) && player.getClass() == KlonePlayerEntity.class) {
                 player.kill();
            }
        }
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "NEW", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private ServerPlayNetworkHandler customNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        if (player instanceof KlonePlayerEntity) {
            return new KloneNetworkHandler(this.server, connection, player);
        } else {
            return new ServerPlayNetworkHandler(this.server, connection, player);
        }
    }
}
