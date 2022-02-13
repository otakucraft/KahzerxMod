package com.kahzerx.kahzerxmod.extensions.achusExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class AchusCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, AchusExtension achus) {
        dispatcher.register(literal("achus").
                requires(server -> achus.getSettings().isEnabled()).
                executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Formatting.GREEN + "Salud"), false);
                    return 1;
                }));
    }
}
