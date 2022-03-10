package com.kahzerx.kahzerxmod.mixin.antiXRayExtension;

import com.google.common.collect.Queues;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.helpers.PacketQueue;
import com.kahzerx.kahzerxmod.extensions.antiXRayExtension.interfaces.ChunkPacketInterface;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow private Channel channel;

    @Shadow public abstract void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback);

    private final Queue<PacketQueue> queuedPackets = Queues.newConcurrentLinkedQueue();

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At(value = "INVOKE", target = "Ljava/util/Queue;add(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void onAddToQueue(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        queuedPackets.add(new PacketQueue(packet, callback));
        ci.cancel();
    }

    @Redirect(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;sendQueuedPackets()V"))
    private void onSend(ClientConnection instance) { }

    @Redirect(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isOpen()Z"))
    private boolean onIsOpen(ClientConnection instance, Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        return instance.isOpen() && this.sendQueue() && isReady(packet);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;sendQueuedPackets()V"))
    private void onSendPackets(ClientConnection instance) {
        sendQueue();
    }

    private boolean sendQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            synchronized (this.queuedPackets) {
                while (!this.queuedPackets.isEmpty()) {
                    PacketQueue packet = queuedPackets.peek();
                    if (!isReady(packet.getPacket())) {
                        return false;
                    } else {
                        this.queuedPackets.poll();
                        this.send(packet.getPacket(), packet.getCallback());
                    }
                }
            }
        }
        return true;
    }

    private boolean isReady(Packet<?> packet) {
        if (packet instanceof ChunkDataS2CPacket combinedPacket) {
            return ((ChunkPacketInterface) combinedPacket).isReady();
        } else {
            return true;
        }
    }
}
