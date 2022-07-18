package com.kahzerx.kahzerxmod.extensions.shopExtension.parcel;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Parcels {
    private List<Parcel> parcels = new ArrayList<>();

    public Parcels() {

    }

    public void createParcels(List<Parcel> p) {
        parcels = p;
    }

    public void addParcel(Parcel p) {
        parcels.add(p);
    }

    public void remove(Parcel p) {
        parcels.remove(p);
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public boolean isPosInParcel(int dim, BlockPos pos) {
        return getParcel(dim, pos) != null;
    }

    public Parcel getParcel(int dim, BlockPos pos) {
        for (Parcel parcel : this.parcels) {
            if (dim != parcel.getDim()) {
                continue;
            }
            if (pos.getX() >= parcel.getCorner1().getX() && pos.getX() <= parcel.getCorner2().getX() && pos.getY() >= parcel.getCorner1().getY() && pos.getY() <= parcel.getCorner2().getY() && pos.getZ() >= parcel.getCorner1().getZ() && pos.getZ() <= parcel.getCorner2().getZ()) {
                return parcel;
            }
        }
        return null;
    }
}
