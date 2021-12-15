package com.kahzerx.kahzerxmod.extensions.maintenanceExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.KahzerxServer;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class MaintenanceExtension extends GenericExtension implements Extensions {
    public static boolean isExtensionEnabled = false;

    public MaintenanceExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        isExtensionEnabled = this.getSettings().isEnabled();
    }

    @Override
    public void onExtensionEnabled() {
        isExtensionEnabled = true;
        for (ServerPlayerEntity player : KahzerxServer.minecraftServer.getPlayerManager().getPlayerList()) {
            if (!KahzerxServer.minecraftServer.getPlayerManager().isOperator(player.getGameProfile())) {
                player.networkHandler.disconnect(new LiteralText("Server is closed for maintenance"));
            }
        }
    }

    @Override
    public void onExtensionDisabled() {
        isExtensionEnabled = false;
    }
}
