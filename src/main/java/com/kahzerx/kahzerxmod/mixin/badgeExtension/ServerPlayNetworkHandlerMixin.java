package com.kahzerx.kahzerxmod.mixin.badgeExtension;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    // TODO hacer las badges que se muestren en el chat.
//    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
//    private Text onGetDisplayName(ServerPlayerEntity instance) {
//        MutableText name = (MutableText) instance.getDisplayName();
//        if (BadgeExtension.isExtensionEnabled) {
//            List<BadgeInstance> badges = BadgeExtension.playerBadges.get(instance.getUuidAsString());
//            if (badges.size() != 0) {
//                name.append(Text.literal(" "));
//            }
//            for (int i = badges.size() - 1; i >= 0; i--) {
//                int finalI = i;
//                name.append(Text.literal(badges.get(i).getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badges.get(finalI).getColorIndex()))));
//                if (i < badges.size() - 2) {
//                    break;
//                }
//            }
//        }
//        return name;
//    }
}
