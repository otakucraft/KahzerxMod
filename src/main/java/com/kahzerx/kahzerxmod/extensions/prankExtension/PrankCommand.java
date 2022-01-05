package com.kahzerx.kahzerxmod.extensions.prankExtension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PrankCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, PrankExtension prankExtension) {
        dispatcher.register(literal("prank").
                requires(server -> prankExtension.extensionSettings().isEnabled()).
                then(argument("level", IntegerArgumentType.integer(0, 5)).
                        suggests(((context, builder) -> CommandSource.suggestMatching(new String[]{"0", "1", "2", "3", "4", "5"}, builder))).
                        executes(context -> {
                            prankExtension.updateLevel(context.getSource().getPlayer(), PrankLevel.idToLevel(IntegerArgumentType.getInteger(context, "level")));
                            context.getSource().sendFeedback(new LiteralText(String.format("Prank level > %d", IntegerArgumentType.getInteger(context, "level"))), false);
                            return 1;
                        })).
                then(literal("info").
                        then(argument("level", IntegerArgumentType.integer(0, 5)).
                                suggests(((context, builder) -> CommandSource.suggestMatching(new String[]{"0", "1", "2", "3", "4", "5"}, builder))).
                                executes(context -> {
                                    PrankLevel level = PrankLevel.idToLevel(IntegerArgumentType.getInteger(context, "level"));
                                    context.getSource().sendFeedback(new LiteralText(
                                            String.format("Nivel %d", level.getID())
                                    ).styled(style -> style.
                                            withBold(true).
                                            withColor(level.getFormatting())
                                    ).append(new LiteralText(
                                            String.format(": %s", level.getDescription())
                                    ).styled(style -> style.
                                            withBold(false).
                                            withColor(Formatting.WHITE))), false);
                                    return 1;
                                }))));
    }
}
