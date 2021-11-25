package com.kahzerx.kahzerxmod.extensions.spoofExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
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
        final int invSize = 54;
        final int hotBarSize = 9;
        final int hotBarStartPos = 27;
        final int invStartPos = 9;
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

        final int armorSlotStartPos = 45;
        for (int j = 0; j < player2.getInventory().armor.size(); j++) {
            inventory.setStack(j + armorSlotStartPos, player2.getInventory().armor.get(j));
        }

        final int offHandSlotPos = 36;
        inventory.setStack(offHandSlotPos, player2.getInventory().offHand.get(0));

        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (i, playerInventory, playerEntity) ->
                                GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory),
                        new LiteralText(
                                String.format(
                                        "%s stop hax >:(",
                                        player.getName().getString()))));
        return 1;
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }
}
