package com.kahzerx.kahzerxmod.klone;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

public class KloneNetworkManager extends ClientConnection {
    public KloneNetworkManager(NetworkSide side) {
        super(side);
    }

    @Override
    public void disableAutoRead() {
    }

    @Override
    public void handleDisconnection() {
    }
}
