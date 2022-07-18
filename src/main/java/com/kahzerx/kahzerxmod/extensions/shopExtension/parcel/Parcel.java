package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import net.minecraft.util.math.BlockPos;

import java.sql.Timestamp;


public class Parcel {
    private String ownerUUID;
    private final BlockPos corner1;
    private final BlockPos corner2;
    private String name;
    private int price;
    private final int dim;
    private Timestamp nextPayout;

    public Parcel(String ownerUUID, BlockPos corner1, BlockPos corner2, String name, int price, int dim, Timestamp nextPayout) {
        this.ownerUUID = ownerUUID;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.name = name;
        this.price = price;
        this.dim = dim;
        this.nextPayout = nextPayout;
    }

    public BlockPos getCorner1() {
        return corner1;
    }

    public BlockPos getCorner2() {
        return corner2;
    }

    public int getDim() {
        return dim;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public Timestamp getNextPayout() {
        return nextPayout;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasOwner() {
        return this.ownerUUID != null;
    }

    public void setNextPayout(Timestamp nextPayout) {
        this.nextPayout = nextPayout;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
