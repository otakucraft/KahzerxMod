package com.kahzerx.kahzerxmod.mixin.noReports;

import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessagePacketMixin {
    @Inject(method = "signature", at = @At("HEAD"), cancellable = true)
    private void onSignature(CallbackInfoReturnable<MessageSignatureData> cir) {
        cir.setReturnValue(MessageSignatureData.EMPTY);
    }
}
