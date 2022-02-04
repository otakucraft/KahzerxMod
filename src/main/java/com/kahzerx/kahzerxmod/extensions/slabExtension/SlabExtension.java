package com.kahzerx.kahzerxmod.extensions.slabExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class SlabExtension extends GenericExtension implements Extensions {
    public static boolean isExtensionEnabled = false;

    public SlabExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        isExtensionEnabled = this.getSettings().isEnabled();
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        isExtensionEnabled = false;
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        isExtensionEnabled = true;
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new SlabCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }
}
