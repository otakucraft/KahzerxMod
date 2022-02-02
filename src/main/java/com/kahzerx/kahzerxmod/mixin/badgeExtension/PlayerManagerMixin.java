package com.kahzerx.kahzerxmod.mixin.badgeExtension;

import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeExtension;
import com.kahzerx.kahzerxmod.extensions.badgeExtension.BadgeInstance;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void onBroadcast(ServerPlayerEntity instance, Text message, MessageType type, UUID sender) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(sender);
        if (BadgeExtension.isExtensionEnabled && player != null) {
            MutableText t = new LiteralText(String.format("Name: %s\n", player.getName().getString()));
            t.append(new LiteralText(String.format("%s\n", sender.toString().replaceAll("-", ""))));
            if (!BadgeExtension.playerBadges.get(sender.toString()).isEmpty()) {
                t.append(new LiteralText("\nBadges:\n"));
            }
            for (BadgeInstance badgeInstance : BadgeExtension.playerBadges.get(sender.toString())) {
                t.append(new LiteralText("\n" + badgeInstance.getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badgeInstance.getColorIndex()))));
                t.append(new LiteralText(String.format(": %s", badgeInstance.getDescription())).styled(style -> style.withColor(Formatting.WHITE)));
            }

            for (Object o : ((TranslatableText) message).getArgs()) {
                if (o instanceof LiteralText) {
                    LiteralText t2 = new LiteralText(((LiteralText) o).getSiblings().get(2).getString());
                    List<BadgeInstance> badges = BadgeExtension.playerBadges.get(sender.toString());
                    for (int i = badges.size() - 1; i >= 0; i--) {
                        int finalI = i;
                        t2.append(new LiteralText(" " + badges.get(i).getBadge()).styled(style -> style.withColor(Formatting.byColorIndex(badges.get(finalI).getColorIndex()))));
                        if (i < badges.size() - 2) {
                            break;
                        }
                    }
                    ((LiteralText) o).getSiblings().set(2, t2);
                    ((LiteralText) o).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, t)));
                }
            }
        }
        instance.sendMessage(message, type, sender);
    }
}
