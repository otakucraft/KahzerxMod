package com.kahzerx.kahzerxmod.extensions.slabExtension;

import com.kahzerx.kahzerxmod.extensions.slabExtension.utils.SlabUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class SlabCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, SlabExtension slab) {
        dispatcher.register(literal("slab").
                requires(server -> slab.extensionSettings().isEnabled()).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (!SlabUtils.isSlab(player.getMainHandStack().getItem())) {
                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("You need a slab on your main hand"), false);
                        return 1;
                    }
                    if (player.getMainHandStack().hasEnchantments()) {
                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Slab is already enchanted"), false);
                        return 1;
                    }
                    player.getMainHandStack().addEnchantment(Enchantments.POWER, 0);
                    context.getSource().sendFeedback(MarkEnum.TICK.appendMessage("Slab ready!"), false);
                    return 1;
                }));
    }
}
