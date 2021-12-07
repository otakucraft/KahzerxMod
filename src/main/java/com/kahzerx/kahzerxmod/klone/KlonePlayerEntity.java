package com.kahzerx.kahzerxmod.klone;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Objects;

public class KlonePlayerEntity extends ServerPlayerEntity {
    public KlonePlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile);
    }

    public static void createKlone(MinecraftServer server, ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        GameProfile profile = player.getGameProfile();

        server.getPlayerManager().remove(player);
        player.networkHandler.disconnect(new LiteralText("A clone has been created.\nThe clone will leave once you rejoin.\nHappy AFK!"));

        KlonePlayerEntity klonedPlayer = new KlonePlayerEntity(server, world, profile);
        server.getPlayerManager().onPlayerConnect(new KloneNetworkManager(NetworkSide.SERVERBOUND), klonedPlayer);

        klonedPlayer.setHealth(player.getHealth());
        klonedPlayer.networkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        klonedPlayer.interactionManager.changeGameMode(player.interactionManager.getGameMode());
        klonedPlayer.stepHeight = 0.6F;
        klonedPlayer.dataTracker.set(PLAYER_MODEL_PARTS, player.getDataTracker().get(PLAYER_MODEL_PARTS));
        klonedPlayer.getAbilities().flying = player.getAbilities().flying;

        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(klonedPlayer, (byte) (player.headYaw * 256 / 360)), klonedPlayer.world.getRegistryKey());
        server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, klonedPlayer));
        player.getWorld().getChunkManager().updatePosition(klonedPlayer);
    }

    private void getOut() {
        if (this.getVehicle() instanceof PlayerEntity) {
            this.stopRiding();
        }
        this.getPassengersDeep().forEach(entity -> {
            if (entity instanceof PlayerEntity) {
                entity.stopRiding();
            }
        });
    }

    private void kill(Text reason) {
        this.getOut();
        Objects.requireNonNull(this.getServer()).send(new ServerTask(this.getServer().getTicks(), () -> this.networkHandler.onDisconnected(reason)));
    }

    @Override
    public void kill() {
        this.kill(new LiteralText("Killed"));
    }

    @Override
    public void tick() {
        if (Objects.requireNonNull(this.getServer()).getTicks() % 10 == 0) {
            this.networkHandler.syncWithPlayerPosition();
            this.getWorld().getChunkManager().updatePosition(this);
            this.onTeleportationDone();
        }
        super.tick();
        this.playerTick();
    }

    @Override
    public void onDeath(DamageSource source) {
        this.getOut();
        super.onDeath(source);
        this.setHealth(20);
        this.hungerManager = new HungerManager();
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }
}
