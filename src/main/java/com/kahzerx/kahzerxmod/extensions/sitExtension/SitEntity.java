package com.kahzerx.kahzerxmod.extensions.sitExtension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;

public class SitEntity extends ArmorStandEntity implements SitEntityInterface {
    private boolean sitEntity = false;
    public SitEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean isSitEntity() {
        return sitEntity;
    }

    private byte setBitField(byte value, boolean set) {
        return set ? (byte)(value | 16) : (byte)(value & ~16);
    }

    @Override
    public void setSitEntity(boolean sitEntity) {
        this.sitEntity = sitEntity;
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField((Byte)this.dataTracker.get(ARMOR_STAND_FLAGS), sitEntity));
        this.setInvisible(sitEntity);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (this.isSitEntity()) {
            this.setPosition(this.getX(), this.getY() + 0.16, this.getZ());
            this.kill();
        }
        super.removePassenger(passenger);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.sitEntity) {
            nbt.putBoolean("SitEntity", true);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("SitEntity", NbtElement.BYTE_TYPE)) {
            this.sitEntity = nbt.getBoolean("SitEntity");
        }
    }
}
