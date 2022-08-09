package com.kahzerx.kahzerxmod.mixin.noReports;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public abstract void sendPacket(Packet<?> packet);

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At(value = "HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (packet instanceof MessageHeaderS2CPacket) {
            ci.cancel();
        } else if (packet instanceof ChatMessageS2CPacket chatPacket) {
            sendPacket(new GameMessageS2CPacket(chatPacket.serializedParameters().toParameters(this.player.world.getRegistryManager()).get().applyChatDecoration(chatPacket.message().getContent()), false));
            ci.cancel();
        }
    }
}
