package com.kahzerx.kahzerxmod.extensions.shopExtension.exchange;

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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExchangeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopExtension extension) {
        dispatcher.register(literal("exchange").
                requires(server -> extension.extensionSettings().isEnabled()).
                then(literal("confirm").
                        then(argument("count", IntegerArgumentType.integer(0)).
                                then(argument("item", StringArgumentType.string()).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            Item item = player.getMainHandStack().getItem();
                                            String sentItem = StringArgumentType.getString(context, "item");
                                            int count = player.getMainHandStack().getCount();
                                            int sentCount = IntegerArgumentType.getInteger(context, "count");
                                            if (!basicVerify(context, player)) {
                                                return 1;
                                            }
                                            if (count != sentCount || !item.getTranslationKey().equals(sentItem)) {
                                                context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("El stack de tu mano ha cambiado!"), false);
                                                return 1;
                                            }
                                            extension.updateFounds(player, Exchange.getValue(item) * count);
                                            extension.logExchange(player, item, count);
                                            player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.AIR));
                                            context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Añadidos ").append(MarkEnum.OTAKU_COIN.appendMessage(String.valueOf(Exchange.getValue(item) * count))), false);
                                            return 1;
                                        })))).
                then(literal("abort").
                        then(argument("count", IntegerArgumentType.integer(0)).
                                then(argument("item", StringArgumentType.string()).
                                        executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
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
                    Item item = player.getMainHandStack().getItem();
                    int count = player.getMainHandStack().getCount();
                    if (!basicVerify(context, player)) {
                        return 1;
                    }
                    context.getSource().sendFeedback(
                            MarkEnum.INFO.appendMessage(String.format("Vas a cambiar %d %s por ", count, item.getName().getString())).
                                    append(MarkEnum.OTAKU_COIN.appendMessage(String.format("%d", Exchange.getValue(item) * count))),
                            false);
                    context.getSource().sendFeedback(
                            MarkEnum.QUESTION.appendMessage("Confirmar exchange? ").
                                    append(new LiteralText("[Sí]").styled(style -> style.withColor(Formatting.GREEN).
                                            withBold(true).
                                            withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/exchange confirm %d %s", count, item.getTranslationKey()))).
                                            withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("confirmar exchange"))))).
                                    append(new LiteralText(" ").styled(style -> style.withColor(Formatting.WHITE))).
                                    append(new LiteralText("[No]").styled(style -> style.withColor(Formatting.DARK_RED).
                                            withBold(true).
                                            withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/exchange abort %d %s", count, item.getTranslationKey()))).
                                            withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("cancelar exchange"))))),
                            false
                    );
                    return 1;
                }).
                then(literal("info").
                        executes(context -> {
                            context.getSource().sendFeedback(MarkEnum.OTAKU_COIN.appendMessage("a"), false);
                            return 1;
                        })));
    }

    private MutableText getExchangeHelpCommand() {
        return new LiteralText("/exchange info").styled(style -> style.withColor(Formatting.DARK_GREEN).
                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exchange info")).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Info about the exchange"))));
    }

    private boolean basicVerify(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        if (player.getMainHandStack().isEmpty()) {
            context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Necesitas el item a cambiar en la mano!"), false);
            context.getSource().sendFeedback(MarkEnum.INFO.appendText(new LiteralText("Usa ").append(getExchangeHelpCommand())), false);
            return false;
        }
        if (!Exchange.isValidItem(item)) {
            context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("El item no es válido!"), false);
            context.getSource().sendFeedback(MarkEnum.INFO.appendText(new LiteralText("Usa ").append(getExchangeHelpCommand())), false);
            return false;
        }
        return true;
    }
}
