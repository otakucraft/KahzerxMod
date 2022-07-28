package com.kahzerx.kahzerxmod.extensions.fbiExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import static com.kahzerx.kahzerxmod.extensions.fbiExtension.FBIExtension.getHiddenPlayers;
import static net.minecraft.server.command.CommandManager.literal;

public class FBICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, FBIExtension fbi) {
        dispatcher.register(literal("fbi").
                requires(player -> fbi.extensionSettings().isEnabled() && player.hasPermissionLevel(2)).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player == null) {
                        return 1;
                    }
                    MinecraftServer server = player.getServer();
                    if (server == null) {
                        return 1;
                    }
                    if (getHiddenPlayers().contains(player)) {
                        getHiddenPlayers().remove(player);
                        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                            if (p.equals(player)) {
                                continue;
                            }
                            p.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));
                            p.networkHandler.sendPacket(new PlayerSpawnS2CPacket(player));
                        }
                        server.getPlayerManager().broadcast(Text.translatable("multiplayer.player.joined", new Object[]{player.getDisplayName()}).formatted(Formatting.YELLOW), false);
                    } else {
                        getHiddenPlayers().add(player);
                        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                            if (p.equals(player)) {
                                continue;
                            }
                            p.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, player));
                            p.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(player.getId()));
                        }
                        player.changeGameMode(GameMode.SPECTATOR);
//                        server.getPlayerManager().broadcast(Text.translatable("multiplayer.player.joined", new Object[]{player.getDisplayName()}).formatted(Formatting.YELLOW), MessageType.SYSTEM);
                        server.getPlayerManager().broadcast(Text.translatable("multiplayer.player.left", new Object[]{player.getDisplayName()}).formatted(Formatting.YELLOW), false);
                    }
                    return 1;
                }));
    }
}
