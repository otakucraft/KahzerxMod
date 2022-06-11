package com.kahzerx.kahzerxmod.mixin.badgeExtension;

import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeExtension;
import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeInstance;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private void onBroadcast(ServerPlayerEntity instance, SignedMessage message, MessageSender sender, RegistryKey<MessageType> typeKey) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(sender.uuid());
        if (BadgeExtension.isExtensionEnabled && player != null) {
            MutableText t = Text.literal(String.format("Name: %s", player.getName().getString()));
            if (!BadgeExtension.playerBadges.get(sender.uuid().toString()).isEmpty()) {
                t.append(Text.literal("\nBadges:"));
            }
            for (BadgeInstance badgeInstance : BadgeExtension.playerBadges.get(sender.toString())) {
                t.append(Text.literal("\n" + badgeInstance.getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badgeInstance.getColorIndex()))));
                t.append(Text.literal(String.format(": %s", badgeInstance.getDescription())).styled(style -> style.withColor(Formatting.WHITE)));
            }
            // TODO clavarle el mensaje este.
//            ((TranslatableTextContent) message)
//            ((LiteralTextContent) ((TranslatableTextContent) message).getArgs()[0]).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, t)));
        }
        instance.sendChatMessage(message, sender, typeKey);
    }
}
