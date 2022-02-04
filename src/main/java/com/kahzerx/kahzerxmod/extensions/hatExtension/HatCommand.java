package com.kahzerx.kahzerxmod.extensions.hatExtension;

import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.literal;

public class HatCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, HatExtension hat) {
        dispatcher.register(literal("hat").
                requires(server -> hat.extensionSettings().isEnabled()).
                executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player.getMainHandStack().isEmpty()) {
                        context.getSource().sendFeedback(MarkEnum.CROSS.appendMessage("Hold something on your main hand"), false);
                        return 1;
                    }
                    int mainHandStack = player.getInventory().getSlotWithStack(player.getMainHandStack());
                    ItemStack mainHand = player.getInventory().getStack(mainHandStack);
                    ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
                    player.equipStack(EquipmentSlot.HEAD, mainHand);
                    player.setStackInHand(Hand.MAIN_HAND, head);
                    return 1;
                }));
    }
}
