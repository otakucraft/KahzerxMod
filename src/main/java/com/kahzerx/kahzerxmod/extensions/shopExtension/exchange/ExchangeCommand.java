package com.kahzerx.kahzerxmod.extensions.shopExtension.exchange;

import com.kahzerx.kahzerxmod.extensions.shopExtension.BankInstance;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExchangeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("exchange").
                requires(server -> extension.extensionSettings().isEnabled()).
                then(literal("getBack").
                        then(argument("item", StringArgumentType.string()).
                                executes(context -> {
                                    context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("Debes especificar cuanto quieres cambiar!"), false);
                                    return 1;
                                }).
                                then(argument("count", IntegerArgumentType.integer(1)).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player == null) {
                                                return 1;
                                            }
                                            String selectedItem = StringArgumentType.getString(context, "item");
                                            int sentCount = IntegerArgumentType.getInteger(context, "count");
                                            Item foundItem = null;
                                            for (Item item : Exchange.getValidExchanges().keySet()) {
                                                if (item.getTranslationKey().equals(selectedItem)) {
                                                    foundItem = item;
                                                    break;
                                                }
                                            }
                                            if (foundItem == null) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("No es un item válido!"), false);
                                                return 1;
                                            }
                                            BankInstance.Exchanges exchanges = extension.getAccounts().get(player).getExchanges();
                                            int actualCount = exchanges.getFromItem(foundItem);
                                            if (actualCount >= sentCount) {
                                                int moneyEQ = Exchange.getValue(foundItem) * sentCount;
                                                if (extension.getAccounts().get(player).getCoins() <= moneyEQ) {
                                                    context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("No tienes suficientes otakucoins!"), false);
                                                    return 1;
                                                }
                                                extension.getDB().getQuery().updateFounds(player, moneyEQ * -1, extension.getAccounts());
                                                extension.getDB().getQuery().logExchange(player, foundItem, sentCount * -1, extension.getAccounts());

                                                ItemScatterer.spawn(player.getWorld(), player.getX(), player.getY(), player.getZ(), new ItemStack(foundItem, sentCount));
                                                context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Cambio realizado!"), false);
                                            } else {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Demasiados items!"), false);
                                            }
                                            return 1;
                                        })))).
                then(literal("confirm").
                        then(argument("count", IntegerArgumentType.integer(0)).
                                then(argument("item", StringArgumentType.string()).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player == null) {
                                                return 1;
                                            }
                                            Item item = player.getMainHandStack().getItem();
                                            String sentItem = StringArgumentType.getString(context, "item");
                                            int count = player.getMainHandStack().getCount();
                                            int sentCount = IntegerArgumentType.getInteger(context, "count");
                                            if (isInvalid(context, player)) {
                                                return 1;
                                            }
                                            if (count != sentCount || !item.getTranslationKey().equals(sentItem)) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("El stack de tu mano ha cambiado!"), false);
                                                return 1;
                                            }
                                            extension.getDB().getQuery().updateFounds(player, Exchange.getValue(item) * count, extension.getAccounts());
                                            extension.getDB().getQuery().logExchange(player, item, count, extension.getAccounts());
                                            player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.AIR));
                                            context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Añadidos ").append(MarkEnum.OTAKU_COIN.appendMessage(String.valueOf(Exchange.getValue(item) * count))), false);
                                            return 1;
                                        })))).
                then(literal("abort").
                        then(argument("count", IntegerArgumentType.integer(0)).
                                then(argument("item", StringArgumentType.string()).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player == null) {
                                                return 1;
                                            }
                                            Item item = player.getMainHandStack().getItem();
                                            String sentItem = StringArgumentType.getString(context, "item");
                                            int count = player.getMainHandStack().getCount();
                                            int sentCount = IntegerArgumentType.getInteger(context, "count");
                                            if (count == sentCount || item.getTranslationKey().equals(sentItem)) {
                                                context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Exchange cancelado!"), false);
                                            }
                                            return 1;
                                        })))).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player == null) {
                        return 1;
                    }
                    Item item = player.getMainHandStack().getItem();
                    int count = player.getMainHandStack().getCount();
                    if (item == Items.AIR) {
                        context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("Haz click sobre el item que quieres cambiar?"), false);
                        BankInstance.Exchanges exchanges = extension.getAccounts().get(player).getExchanges();
                        context.getSource().sendFeedback(getFormattedGetBack(Items.DIAMOND, exchanges.getDiamond(), true), false);
                        context.getSource().sendFeedback(getFormattedGetBack(Items.DIAMOND_BLOCK, exchanges.getDiamondBlock(), true), false);
                        context.getSource().sendFeedback(getFormattedGetBack(Items.NETHERITE_INGOT, exchanges.getNetheriteIngot(), true), false);
                        context.getSource().sendFeedback(getFormattedGetBack(Items.NETHERITE_BLOCK, exchanges.getNetheriteBlock(), true), false);
                        context.getSource().sendFeedback(getFormattedGetBack(Items.NETHERITE_SCRAP, exchanges.getNetheriteScrap(), true), false);
                        context.getSource().sendFeedback(getFormattedGetBack(Items.ANCIENT_DEBRIS, exchanges.getDebris(), true), false);
                        context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("Al hacer click especifica en el comando cuanto quieres cambiar?"), false);
                        context.getSource().sendFeedback(MarkEnum.WARNING.appendMessage("Los items se dropearán al suelo! Verifica que estés en un sitio seguro.", Formatting.GOLD), false);
                        return 1;
                    }
                    if (isInvalid(context, player)) {
                        return 1;
                    }
                    context.getSource().sendFeedback(
                            MarkEnum.INFO.appendMessage(String.format("Vas a cambiar %d %s por ", count, item.getName().getString())).
                                    append(MarkEnum.OTAKU_COIN.appendMessage(String.format("%d", Exchange.getValue(item) * count))),
                            false);
                    context.getSource().sendFeedback(
                            MarkEnum.QUESTION.appendMessage("Confirmar exchange? ").
                                    append(Text.literal("[Sí]").styled(style -> style.withColor(Formatting.GREEN).
                                            withBold(true).
                                            withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/exchange confirm %d %s", count, item.getTranslationKey()))).
                                            withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("confirmar exchange"))))).
                                    append(Text.literal(" ").styled(style -> style.withColor(Formatting.WHITE))).
                                    append(Text.literal("[No]").styled(style -> style.withColor(Formatting.DARK_RED).
                                            withBold(true).
                                            withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/exchange abort %d %s", count, item.getTranslationKey()))).
                                            withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("cancelar exchange"))))),
                            false
                    );
                    return 1;
                }).
                then(literal("info").
                        executes(context -> {
                            context.getSource().sendFeedback(
                                    Text.literal("\n").
                                    append(MarkEnum.OTAKU_COIN.getFormattedIdentifier().append(Text.literal(" ").styled(style -> style.
                                            withUnderline(false)))).
                                    append(Text.literal("Economía").styled(style -> style.
                                            withColor(Formatting.GREEN).withUnderline(true))).
                                    append(Text.literal(" ").append(MarkEnum.OTAKU_COIN.getFormattedIdentifier()).append("\n\n").styled(style -> style.
                                            withUnderline(false))),
                                    false
                            );
                            for (Item item : Exchange.getValidExchanges().keySet()) {
                                context.getSource().sendFeedback(getFormattedGetBack(item, Exchange.getValue(item), false).append(MarkEnum.OTAKU_COIN.getFormattedIdentifier()), false);
                            }
                            return 1;
                        })));
    }

    private MutableText getFormattedGetBack(Item item, int amount, boolean withGetBack) {
        int updated = amount == -1 ? 0 : amount;
        MutableText t;
        if (item == Items.DIAMOND || item == Items.DIAMOND_BLOCK) {
            t = MarkEnum.DIAMOND_LIKE.boldAppendMessage(item.getName().getString() + ": " + updated, updated == 0 ? Formatting.GRAY : Formatting.WHITE);
        } else {
            t = MarkEnum.NETHERITE_LIKE.boldAppendMessage(item.getName().getString() + ": " + updated, updated == 0 ? Formatting.GRAY : Formatting.WHITE);
        }
        if (withGetBack && updated != 0) {
            t.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/exchange getBack %s ", item.getTranslationKey()))));
        }
        return t;
    }

    private MutableText getExchangeHelpCommand() {
        return Text.literal("/exchange info").styled(style -> style.withColor(Formatting.DARK_GREEN).
                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exchange info")).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Info about the exchange"))));
    }

    private boolean isInvalid(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        if (player.getMainHandStack().isEmpty()) {
            context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Necesitas el item a cambiar en la mano!"), false);
            context.getSource().sendFeedback(MarkEnum.INFO.appendText(Text.literal("Usa ").append(getExchangeHelpCommand())), false);
            return true;
        }
        if (!Exchange.isValidItem(item)) {
            context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("El item no es válido!"), false);
            context.getSource().sendFeedback(MarkEnum.INFO.appendText(Text.literal("Usa ").append(getExchangeHelpCommand())), false);
            return true;
        }
        return false;
    }
}
