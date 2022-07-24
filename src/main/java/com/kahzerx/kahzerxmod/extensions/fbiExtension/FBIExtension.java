package com.kahzerx.kahzerxmod.extensions.fbiExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class FBIExtension extends GenericExtension implements Extensions {
    public static List<ServerPlayerEntity> hiddenPlayers = new ArrayList<>();
    public FBIExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        hiddenPlayers.remove(player);
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new FBICommand().register(dispatcher, this);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    public static List<ServerPlayerEntity> getHiddenPlayers() {
        return hiddenPlayers;
    }
}
