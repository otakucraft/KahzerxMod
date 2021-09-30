package com.kahzerx.kahzerxmod.extensions.seedExtension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class KSeedCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SeedExtension extension) {
        dispatcher.register(literal("seed").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2) || extension.getSettings().isEnabled()).
                executes(context -> {
                    long l = context.getSource().getWorld().getSeed();
                    Text text = Texts.bracketed((new LiteralText(String.valueOf(l))).styled((style) -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(l))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click"))).withInsertion(String.valueOf(l))));
                    context.getSource().sendFeedback(new TranslatableText("commands.seed.success", text), false);
                    return (int)l;
                }));
    }
}
