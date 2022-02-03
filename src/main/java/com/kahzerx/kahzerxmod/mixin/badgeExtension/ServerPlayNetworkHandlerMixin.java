package com.kahzerx.kahzerxmod.mixin.badgeExtension;

import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeExtension;
import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeInstance;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
    private Text onGetDisplayName(ServerPlayerEntity instance) {
        MutableText name = (MutableText) instance.getDisplayName();
        if (BadgeExtension.isExtensionEnabled) {
            List<BadgeInstance> badges = BadgeExtension.playerBadges.get(instance.getUuidAsString());
            for (int i = badges.size() - 1; i >= 0; i--) {
                int finalI = i;
                name.append(new LiteralText(" " + badges.get(i).getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badges.get(finalI).getColorIndex()))));
                if (i < badges.size() - 2) {
                    break;
                }
            }
        }
        return name;
    }
}
