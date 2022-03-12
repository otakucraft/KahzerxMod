package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientConnection.QueuedPacket.class)
public interface ConnectionQueueAccessor {
    @Accessor("packet")
    Packet<?> getPacket();

    @Accessor("callback")
    GenericFutureListener<? extends Future<? super Void>> getCallback();
}
