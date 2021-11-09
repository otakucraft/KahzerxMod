package com.kahzerx.kahzerxmod.extensions.hatExtension;

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
                    ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
                    ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
                    return 1;
                }));
    }
}
