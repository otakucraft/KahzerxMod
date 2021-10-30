package com.kahzerx.kahzerxmod.extensions.bocaExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class BocaExtension extends GenericExtension implements Extensions {
    public BocaExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new BocaCommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }
}
