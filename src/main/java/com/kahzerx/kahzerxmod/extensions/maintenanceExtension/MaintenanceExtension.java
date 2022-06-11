package com.kahzerx.kahzerxmod.extensions.maintenanceExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MaintenanceExtension extends GenericExtension implements Extensions {
    public static boolean isExtensionEnabled = false;
    private MinecraftServer server = null;

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
        this.server = minecraftServer;
    }

    @Override
    public void onExtensionEnabled() {
        isExtensionEnabled = true;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!this.server.getPlayerManager().isOperator(player.getGameProfile())) {
                player.networkHandler.disconnect(Text.literal("Server is closed for maintenance"));
            }
        }
    }

    @Override
    public void onExtensionDisabled() {
        isExtensionEnabled = false;
    }
}
