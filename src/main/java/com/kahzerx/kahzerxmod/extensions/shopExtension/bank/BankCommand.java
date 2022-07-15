package com.kahzerx.kahzerxmod.extensions.shopExtension.bank;

import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsLevels;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BankCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("bank").
                requires(server -> extension.extensionSettings().isEnabled()).
                then(literal("balance").
                        then(literal("bank").
                                requires(server -> server.hasPermissionLevel(2) || (extension.getPermsExtension().extensionSettings().isEnabled() && extension.getPermsExtension().getDBPlayerPerms(server.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId())).
                                executes(context -> {
                                    context.getSource().sendFeedback(Text.literal("Bank Balance: ").append(MarkEnum.OTAKU_COIN.appendMessage(String.valueOf(extension.getDB().getQuery().getBalance("00000000-0000-0000-0000-000000000000")))), false);
                                    return 1;
                                })).
                        executes(context -> {
                            context.getSource().sendFeedback(Text.literal("Balance: ").append(MarkEnum.OTAKU_COIN.appendMessage(String.valueOf(extension.getDB().getQuery().getBalance(context.getSource().getPlayer())))), false);
                            return 1;
                        })).
                then(literal("transfer").
                        then(literal("from_bank").
                                requires(server -> server.hasPermissionLevel(2) || (extension.getPermsExtension().extensionSettings().isEnabled() && extension.getPermsExtension().getDBPlayerPerms(server.getPlayer().getUuidAsString()).getId() >= PermsLevels.HELPER.getId())).
                                then(argument("player", StringArgumentType.string()).
                                        suggests((c, b) -> suggestMatching(extension.getDB().getQuery().getPlayers(), b)).
                                        then(argument("amount", IntegerArgumentType.integer(1)).
                                                executes(context -> {
                                                    String bankUUID = "00000000-0000-0000-0000-000000000000";
                                                    String playerName = StringArgumentType.getString(context, "player");
                                                    String playerUUID = extension.getDB().getQuery().getPlayerUUID(playerName);
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    if (playerUUID == null) {
                                                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Este jugador no existe!"), false);
                                                        return 1;
                                                    }
                                                    if (bankUUID.equals(playerUUID)) {
                                                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("No te puedes hacer transferencias a ti mismo!"), false);
                                                        return 1;
                                                    }
                                                    if (extension.getBankAccount().getCoins() < amount) {
                                                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("No tienes balance suficiente!"), false);
                                                        return 1;
                                                    }
                                                    extension.getDB().getQuery().updateFounds(playerUUID, amount, extension.getAccounts());
                                                    extension.getDB().getQuery().updateFounds(bankUUID, amount * -1, extension.getAccounts());
                                                    extension.getDB().getQuery().logTransfer(bankUUID, playerUUID, playerName, amount, extension.getAccounts());
                                                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Transferencia de ").append(MarkEnum.OTAKU_COIN.appendMessage(String.format("%d a %s completada!", amount, playerName))), false);
                                                    ServerPlayerEntity destPlayer = context.getSource().getServer().getPlayerManager().getPlayer(UUID.fromString(playerUUID));
                                                    if (destPlayer != null) {
                                                        destPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("El Banco te ha transferido ").append(MarkEnum.OTAKU_COIN.appendMessage(String.valueOf(amount)))));
                                                    }
                                                    return 1;
                                                })))).
                        then(argument("player", StringArgumentType.string()).
                                suggests((c, b) -> suggestMatching(extension.getDB().getQuery().getPlayers(), b)).
                                then(argument("amount", IntegerArgumentType.integer(1)).
                                        executes(context -> {
                                            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
                                            if (sourcePlayer == null) {
                                                return 1;
                                            }
                                            String playerName = StringArgumentType.getString(context, "player");
                                            String playerUUID = extension.getDB().getQuery().getPlayerUUID(playerName);
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            if (playerUUID == null) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Este jugador no existe!"), false);
                                                return 1;
                                            }
                                            if (sourcePlayer.getUuidAsString().equals(playerUUID)) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("No te puedes hacer transferencias a ti mismo!"), false);
                                                return 1;
                                            }
                                            if (extension.getAccounts().get(context.getSource().getPlayer()).getCoins() < amount) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("No tienes balance suficiente!"), false);
                                                return 1;
                                            }
                                            extension.getDB().getQuery().updateFounds(playerUUID, amount, extension.getAccounts());
                                            extension.getDB().getQuery().updateFounds(context.getSource().getPlayer(), amount * -1, extension.getAccounts());
                                            extension.getDB().getQuery().logTransfer(context.getSource().getPlayer(), playerUUID, playerName, amount, extension.getAccounts());
                                            context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Transferencia de ").append(MarkEnum.OTAKU_COIN.appendMessage(String.format("%d a %s completada!", amount, playerName))), false);
                                            ServerPlayerEntity destPlayer = context.getSource().getServer().getPlayerManager().getPlayer(UUID.fromString(playerUUID));
                                            if (destPlayer != null) {
                                                destPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal(context.getSource().getPlayer().getName().getString() + " te ha transferido ").append(MarkEnum.OTAKU_COIN.appendMessage(String.valueOf(amount)))));
                                            }
                                            return 1;
                                        })))));
    }
}