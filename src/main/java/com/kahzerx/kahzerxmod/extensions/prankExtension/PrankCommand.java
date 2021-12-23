package com.kahzerx.kahzerxmod.extensions.prankExtension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PrankCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, PrankExtension prankExtension) {
        dispatcher.register(literal("prank").
                requires(server -> prankExtension.extensionSettings().isEnabled()).
                then(argument("int", IntegerArgumentType.integer(0, 5)).
                        executes(context -> {
                            prankExtension.updateLevel(context.getSource().getPlayer(), PrankLevel.idToLevel(IntegerArgumentType.getInteger(context, "int")));
                            context.getSource().sendFeedback(new LiteralText(String.format("Prank level > %d", IntegerArgumentType.getInteger(context, "int"))), false);
                            return 1;
                        })));
    }
}
