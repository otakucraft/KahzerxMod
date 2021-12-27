package com.kahzerx.kahzerxmod.extensions.spoofExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class SpoofExtension extends GenericExtension implements Extensions {
    public SpoofExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new SpoofCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public int spoofEC(ServerCommandSource source, String playerE) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        ServerPlayerEntity player2 = source.getServer().getPlayerManager().getPlayer(playerE);
        if (player2 != null) {
            EnderChestInventory enderChestInventory = player2.getEnderChestInventory();
            // Generar la pantalla de enderChest.
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, enderChestInventory),
                    new LiteralText(String.format("%s stop hax >:(", player.getName().getString()))));
        } else {
            source.sendFeedback(new LiteralText("player offline"), false);
        }
        return 1;
    }

    public int spoofInv(ServerCommandSource source, String playerE) throws CommandSyntaxException {
        int invSize = 54;
        int hotBarSize = 9;
        int hotBarStartPos = 27;
        int invStartPos = 9;
        Inventory inventory = new SimpleInventory(invSize);
        ServerPlayerEntity player = source.getPlayer();
        ServerPlayerEntity player2 = source.getServer().getPlayerManager().getPlayer(playerE);
        assert player2 != null;

        for (int i = 0; i < player2.getInventory().main.size(); i++) {
            if (i < hotBarSize) {
                inventory.setStack(i + hotBarStartPos, player2.getInventory().main.get(i));
            } else {
                inventory.setStack(i - invStartPos, player2.getInventory().main.get(i));
            }
        }

        int armorSlotStartPos = 45;
        for (int j = 0; j < player2.getInventory().armor.size(); j++) {
            inventory.setStack(j + armorSlotStartPos, player2.getInventory().armor.get(j));
        }

        int offHandSlotPos = 36;
        inventory.setStack(offHandSlotPos, player2.getInventory().offHand.get(0));

        ScreenHandlerListener listener = new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                // source.getServer().getPlayerManager().saveAllPlayerData();
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

            }
        };

        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (i, playerInventory, playerEntity) -> {
                            GenericContainerScreenHandler invCont = GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                            invCont.addListener(listener);
                            return invCont;
                        }, new LiteralText(String.format("%s stop hax >:(", player.getName().getString()))));
        return 1;
    }
}
