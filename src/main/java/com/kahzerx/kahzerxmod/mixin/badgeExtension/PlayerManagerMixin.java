package com.kahzerx.kahzerxmod.mixin.badgeExtension;

import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeExtension;
import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeInstance;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private void onBroadcast(ServerPlayerEntity instance, SignedMessage message, MessageSender sender, RegistryKey<MessageType> typeKey) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(sender.uuid());
        if (BadgeExtension.isExtensionEnabled && player != null) {
            MutableText t = Text.literal("");
            List<BadgeInstance> badges = BadgeExtension.playerBadges.get(sender.uuid().toString());
            if (badges.size() != 0) {
                MutableText hover = Text.literal("");
                hover.append(Text.literal("Badges:").styled(style -> style.withColor(Formatting.WHITE)));
                for (BadgeInstance badgeInstance : badges) {
                    hover.append(Text.literal("\n" + badgeInstance.getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badgeInstance.getColorIndex()))));
                    hover.append(Text.literal(String.format(": %s", badgeInstance.getDescription())).styled(style -> style.withColor(Formatting.WHITE)));
                }
                MutableText bgs = Text.literal("");
                for (int i = badges.size() - 1; i >= 0; i--) {
                    BadgeInstance badge = badges.get(i);
                    bgs.append(Text.literal(badge.getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badge.getColorIndex()))));
                    bgs.append(Text.literal("").styled(style -> style.withColor(Formatting.WHITE)));
                    if (i < badges.size() - 2 || i == 0) {
                        break;
                    }
                    bgs.append(Text.literal(" "));
                }
                t.append(bgs.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))));
                t.append(Text.literal(" "));
            }
            t.append(message.signedContent());
            SignedMessage sm = new SignedMessage(t, message.signature(), message.unsignedContent());
            instance.sendChatMessage(sm, sender, typeKey);
        } else {
            instance.sendChatMessage(message, sender, typeKey);
        }
    }
}
