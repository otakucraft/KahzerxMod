package com.kahzerx.kahzerxmod.klone;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class KlonePlayerEntity extends ServerPlayerEntity {
    public KlonePlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(server, world, profile, publicKey);
    }

    public static void createKlone(MinecraftServer server, ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        GameProfile profile = player.getGameProfile();

        server.getPlayerManager().remove(player);
        player.networkHandler.disconnect(Text.literal("A clone has been created.\nThe clone will leave once you rejoin.\nHappy AFK!"));

        KlonePlayerEntity klonedPlayer = new KlonePlayerEntity(server, world, profile, player.getPublicKey());
//        KlonePlayerEntity klonedPlayer = new KlonePlayerEntity(server, world, profile);

        klonedPlayer.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        server.getPlayerManager().onPlayerConnect(new KloneNetworkManager(NetworkSide.SERVERBOUND), klonedPlayer);
        klonedPlayer.teleport(world, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        klonedPlayer.setHealth(player.getHealth());
        klonedPlayer.unsetRemoved();
        klonedPlayer.stepHeight = 0.6F;
        klonedPlayer.interactionManager.changeGameMode(player.interactionManager.getGameMode());

        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(klonedPlayer, (byte) (player.headYaw * 256 / 360)), klonedPlayer.world.getRegistryKey());
        server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(klonedPlayer), klonedPlayer.world.getRegistryKey());
        server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, klonedPlayer));

        player.getWorld().getChunkManager().updatePosition(klonedPlayer);

        klonedPlayer.dataTracker.set(PLAYER_MODEL_PARTS, (byte) 0x7f);
        klonedPlayer.getAbilities().flying = player.getAbilities().flying;
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

    public void kill(Text reason) {
        this.getOut();
        this.server.send(new ServerTask(this.server.getTicks(), () -> this.networkHandler.onDisconnected(reason)));
    }

    @Override
    public void kill() {
        this.kill(Text.literal("Killed"));
    }

    @Override
    public void tick() {
        if (Objects.requireNonNull(this.getServer()).getTicks() % 10 == 0) {
            this.networkHandler.syncWithPlayerPosition();
            this.getWorld().getChunkManager().updatePosition(this);
            this.onTeleportationDone();
        }
        try {
            super.tick();
            this.playerTick();
        } catch (NullPointerException ignored) { }
    }

    @Override
    public void onDeath(DamageSource source) {
        this.getOut();
        this.setHealth(20);
        this.hungerManager = new HungerManager();
        Text text = this.getDamageTracker().getDeathMessage();
        if (this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
            this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getDamageTracker(), text), future -> {
                if (!future.isSuccess()) {
                    int i = 256;
                    String string = text.asTruncatedString(256);
                    MutableText text2 = Text.translatable("death.attack.message_too_long", Text.literal(string).formatted(Formatting.YELLOW));
                    MutableText text3 = Text.translatable("death.attack.even_more_magic", this.getDisplayName()).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
                    this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getDamageTracker(), text3));
                }
            });
        }
        this.kill(text);
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }
}
