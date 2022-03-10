package com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;
import org.jetbrains.annotations.Nullable;

public class PacketQueue {
    final Packet<?> packet;
    @Nullable
    final GenericFutureListener<? extends Future<? super Void>> callback;

    public PacketQueue(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        this.packet = packet;
        this.callback = callback;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public @Nullable GenericFutureListener<? extends Future<? super Void>> getCallback() {
        return callback;
    }
}
