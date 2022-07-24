package com.kahzerx.kahzerxmod.mixin.fbiExtension;


import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.kahzerx.kahzerxmod.extensions.fbiExtension.FBIExtension.getHiddenPlayers;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 7))
    public void onConnect(ServerPlayNetworkHandler instance, Packet<?> packet) {
        PlayerListS2CPacket p = (PlayerListS2CPacket) packet;
        if (!isHidden(p.getEntries().get(0).getProfile())) {
            instance.sendPacket(packet);
        }
    }

    private boolean isHidden(GameProfile profile) {
        for (ServerPlayerEntity player : getHiddenPlayers()) {
            if (player.getGameProfile().equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
