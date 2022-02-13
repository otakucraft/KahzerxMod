package com.kahzerx.kahzerxmod.extensions.helperKickExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import net.minecraft.server.MinecraftServer;

public class HelperKickExtension extends GenericExtension implements Extensions {
    public static boolean isExtensionEnabled = false;
    public static PermsExtension permsExtension = null;

    public HelperKickExtension(ExtensionSettings settings, PermsExtension perms) {
        super(settings);
        permsExtension = perms;
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
        Extensions.super.onExtensionEnabled();
        isExtensionEnabled = true;
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        isExtensionEnabled = false;
    }
}
