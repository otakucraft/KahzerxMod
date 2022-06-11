package com.kahzerx.kahzerxmod.mixin.discordWhitelistExtension;

import com.kahzerx.kahzerxmod.extensions.discordExtension.discordWhitelistExtension.DiscordWhitelistExtension;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;


public class PlayerManagerWLMixin {
    @Mixin(PlayerManager.class)
    public static abstract class PlayerNotWhitelisted {
        @Shadow @Final private BannedPlayerList bannedProfiles;

        @Shadow public abstract boolean isWhitelisted(GameProfile profile);

        @Inject(method = "checkCanJoin", at = @At(value = "HEAD"), cancellable = true)
        private void onCheckWhitelisted(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
            if (!bannedProfiles.contains(profile) && DiscordWhitelistExtension.isExtensionEnabled) {
                if (!isWhitelisted(profile)) {
                    cir.setReturnValue(Text.literal(String.format("You are not whitelisted :(\n!add %s on discord.", profile.getName())));
                }
            }
        }
    }
}
