package com.kahzerx.kahzerxmod.extensions.deathMsgExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.DimUtils;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import net.minecraft.server.network.ServerPlayerEntity;

public class DeathMsgExtension extends GenericExtension implements Extensions {
    public DeathMsgExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onPlayerDied(ServerPlayerEntity player) {
        if (this.getSettings().isEnabled()) {
            player.sendMessage(MarkEnum.RIP.appendMessage(String.format("%s %s", DimUtils.getDimensionWithColor(player.world), DimUtils.formatCoords(player.getX(), player.getY(), player.getZ()))), false);
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onExtensionEnabled() { }

    @Override
    public void onExtensionDisabled() { }
}
