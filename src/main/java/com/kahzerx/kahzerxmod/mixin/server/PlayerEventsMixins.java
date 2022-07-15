package com.kahzerx.kahzerxmod.mixin.server;

import com.kahzerx.kahzerxmod.KahzerxServer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

        @Inject(method = "onPlayerConnect", at = @At("TAIL"))
        private void onPlayerConnected(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
            KahzerxServer.onPlayerConnected(player);
        }
    }

    @Mixin(ServerPlayerInteractionManager.class)
    public static class PlayerBreak {
        @Shadow @Final protected ServerPlayerEntity player;

        @Shadow protected ServerWorld world;

        @Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE))
        private void onBroken(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            KahzerxServer.onPlayerBreakBlock(player, world, pos);
        }
    }

    @Mixin(BlockItem.class)
    public static class PlayerPlace {
        @Redirect(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"))
        private void onPlaced(Block instance, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
            instance.onPlaced(world, pos, state, placer, itemStack);
            if (placer instanceof ServerPlayerEntity) {
                KahzerxServer.onPlayerPlaceBlock((ServerPlayerEntity) placer, world, pos);
            }
        }
    }

    @Mixin(ServerPlayerEntity.class)
    public static class PlayerDied {
        @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
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

        @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
        private void onAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
            Text text = Text.translatable("chat.type.advancement." + Objects.requireNonNull(advancement.getDisplay()).getFrame().getId(), owner.getDisplayName(), advancement.toHoverableText());
            KahzerxServer.onAdvancement(text.getString().replace("_", "\\_"));
        }
    }

    @Mixin(ServerPlayerEntity.class)
    public static class PlayerSleeping {
        @Inject(method = "sleep", at = @At("HEAD"))
        private void onSleep(BlockPos pos, CallbackInfo ci) {
            KahzerxServer.onSleep((ServerPlayerEntity) (Object) this);
        }

        @Inject(method = "wakeUp", at = @At("HEAD"))
        private void onWakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
            if (updateSleepingPlayers) {
                KahzerxServer.onWakeUp((ServerPlayerEntity) (Object) this);
            }
        }
    }
}
