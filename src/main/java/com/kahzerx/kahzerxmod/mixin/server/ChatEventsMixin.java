package com.kahzerx.kahzerxmod.mixin.server;

import com.kahzerx.kahzerxmod.KahzerxServer;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatEventsMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At("RETURN"))
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        KahzerxServer.onChatMessage(player, packet.getChatMessage());
    }
}
