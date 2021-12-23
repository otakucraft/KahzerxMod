package com.kahzerx.kahzerxmod.mixin.pranksExtension;

import com.kahzerx.kahzerxmod.extensions.prankExtension.PrankExtension;
import com.kahzerx.kahzerxmod.extensions.prankExtension.PrankLevel;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity {
    public PlayerMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void onGet(CallbackInfoReturnable<Text> cir) {
        MutableText name = (MutableText) this.getDisplayName();
        if (PrankExtension.isExtensionEnabled && PrankExtension.playerLevel.containsKey(this.getUuidAsString()) && PrankExtension.playerLevel.get(this.getUuidAsString()) != PrankLevel.LEVEL0) {
            cir.setReturnValue(name.append(Formatting.RED + " " + PrankExtension.playerLevel.get(this.getUuidAsString()).getIdentifier()));
        }
    }
 }
