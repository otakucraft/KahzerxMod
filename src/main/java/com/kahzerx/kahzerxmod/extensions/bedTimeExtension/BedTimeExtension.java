package com.kahzerx.kahzerxmod.extensions.bedTimeExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class BedTimeExtension extends GenericExtension implements Extensions {
    public BedTimeExtension(ExtensionSettings settings) {
        super(settings);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onPlayerSleep(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null && this.extensionSettings().isEnabled()) {
            server.getPlayerManager().broadcast(MarkEnum.SLEEP.appendMessage(player.getName().getString() + " went to sleep", Formatting.YELLOW), MessageType.SYSTEM);
        }
    }

    @Override
    public void onPlayerWakeUp(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null && this.extensionSettings().isEnabled()) {
            server.getPlayerManager().broadcast(MarkEnum.SLEEP.appendMessage(player.getName().getString() + " woke up", Formatting.YELLOW), MessageType.SYSTEM);
        }
    }
}
