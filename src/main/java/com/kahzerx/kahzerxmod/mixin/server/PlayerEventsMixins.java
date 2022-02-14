package com.kahzerx.kahzerxmod.mixin.server;

import com.kahzerx.kahzerxmod.KahzerxServer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

public class PlayerEventsMixins {
    @Mixin(PlayerManager.class)
    public static class PlayerConn {
        @Inject(method = "remove", at = @At("RETURN"))
        private void onPlayerLeft(ServerPlayerEntity player, CallbackInfo ci) {
            KahzerxServer.onPlayerLeft(player);
        }

        @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;loadPlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/nbt/NbtCompound;", shift = At.Shift.AFTER))
        private void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
            KahzerxServer.onPlayerJoined(player);
        }
    }

    @Mixin(ServerPlayerEntity.class)
    public static class PlayerDied {
        @Inject(method = "onDeath", at = @At("HEAD"))
        private void onPlayerDied(DamageSource source, CallbackInfo ci) {
            KahzerxServer.onPlayerDied((ServerPlayerEntity) (Object) this);
        }

        @Inject(method = "swingHand", at = @At("RETURN"))
        private void onPlayerClicker(Hand hand, CallbackInfo ci) {
            KahzerxServer.onClick((ServerPlayerEntity) (Object) this);
        }
    }

    @Mixin(PlayerAdvancementTracker.class)
    public static class PlayerAdvancement {
        @Shadow private ServerPlayerEntity owner;

        @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
        private void onAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
            Text text = new TranslatableText("chat.type.advancement." + Objects.requireNonNull(advancement.getDisplay()).getFrame().getId(), owner.getDisplayName(), advancement.toHoverableText());
            KahzerxServer.onAdvancement(text.getString().replace("_", "\\_"));
        }
    }
}
