package com.kahzerx.kahzerxmod.mixin.pranksExtension;

import com.kahzerx.kahzerxmod.extensions.prankExtension.PrankExtension;
import com.kahzerx.kahzerxmod.extensions.prankExtension.PrankLevel;
import com.kahzerx.kahzerxmod.klone.KlonePlayerEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;


@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity {
    @Shadow @Final public MinecraftServer server;

    public PlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }


    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void onGet(CallbackInfoReturnable<Text> cir) {
        MutableText name = Text.literal (" ").append(this.getDisplayName());
        if (PrankExtension.isExtensionEnabled && PrankExtension.playerLevel.containsKey(this.getUuidAsString()) && PrankExtension.playerLevel.get(this.getUuidAsString()) != PrankLevel.LEVEL0) {
            name.append(PrankExtension.playerLevel.get(this.getUuidAsString()).getFormatting() + " " + PrankExtension.playerLevel.get(this.getUuidAsString()).getIdentifier());
        }
        ServerPlayerEntity p = server.getPlayerManager().getPlayer(UUID.fromString(this.getUuidAsString()));
        if (p != null && p.getClass() == KlonePlayerEntity.class) {
            name.append(Text.literal(" [Bot]").styled(style -> style.withColor(Formatting.GRAY)));
        }
        cir.setReturnValue(name);
    }
}
