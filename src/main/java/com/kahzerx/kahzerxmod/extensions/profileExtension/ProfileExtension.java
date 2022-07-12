package com.kahzerx.kahzerxmod.extensions.profileExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.ProfileCommand;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.GuiPlayer;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.resources.ShopResources;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.main.MainGui;
import com.kahzerx.kahzerxmod.extensions.profileExtension.gui.panels.resources.MainResources;
import com.kahzerx.kahzerxmod.extensions.shopExtension.ShopExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class ProfileExtension extends GenericExtension implements Extensions {
    public static HashMap<ServerPlayerEntity, GuiPlayer> guis = new HashMap<>();
    public ShopExtension shopExtension;
    public ProfileExtension(ExtensionSettings settings, ShopExtension shopExtension) {
        super(settings);
        this.shopExtension = shopExtension;
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        MainResources.noop();
        ShopResources.noop();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new ProfileCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onTick(MinecraftServer server) {
        try {
            for (ServerPlayerEntity player : guis.keySet()) {
                if (guis.get(player).shouldClose()) {
                    guis.get(player).closeGui();
                    guis.get(player).closePanel();
                    guis.remove(player);
                } else {
                    guis.get(player).tick();
                }
            }
        } catch (ConcurrentModificationException ignored) { }
    }

    @Override
    public void onClick(ServerPlayerEntity player) {
        GuiPlayer g = guis.get(player);
        if (g != null && g.isTracking()) {
            g.onClick();
        }
    }

    public void openGUI(ServerPlayerEntity player) {
        if (!extensionSettings().isEnabled()) {
            return;
        }
        GuiPlayer gui = guis.get(player);
        if (gui == null) {
            gui = new GuiPlayer(player, shopExtension);
            guis.put(player, gui);
        }
        gui.openGui(new MainGui());

        Direction dir = player.getHorizontalFacing().getOpposite();
        BlockPos pos = player.getBlockPos().offset(player.getHorizontalFacing(), 3);

        if (gui.isOpen()) {
            gui.closePanel();
        } else {
            gui.openPanel(pos, dir, 6, 5);
        }
    }
}
