package com.kahzerx.kahzerxmod.mixin.playerDropsSkullExtension;

import com.kahzerx.kahzerxmod.extensions.playerDropsSkullExtension.PlayerDropsSkullExtension;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract World getWorld();

    public PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (PlayerDropsSkullExtension.isExtensionEnabled && source == DamageSource.LIGHTNING_BOLT && !causedByPlayer && random.nextDouble() < 0.20D) {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            NbtCompound compound = stack.getOrCreateNbt();
            compound.putString("SkullOwner", this.getName().asString());
            stack.writeNbt(compound);
            new Thread(() -> {
                try {
                    Thread.sleep(500L);
                    ItemScatterer.spawn(this.getWorld(), this.getX(), this.getY(), this.getZ(), stack);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
