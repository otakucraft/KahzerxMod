package com.kahzerx.kahzerxmod.klone;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class KloneNetworkHandler extends ServerPlayNetworkHandler {
    public KloneNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        super(server, connection, player);
    }

    @Override
    public void sendPacket(Packet<?> packet) { }

    @Override
    public void disconnect(Text reason) {
        if (player instanceof KlonePlayerEntity && reason instanceof TranslatableText && ((TranslatableText) reason).getKey().equals("multiplayer.disconnect.idling")) {
            ((KlonePlayerEntity) player).kill(new TranslatableText(((TranslatableText) reason).getKey()));
        }
    }
}
