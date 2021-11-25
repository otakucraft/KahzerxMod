package com.kahzerx.kahzerxmod.extensions.spawnExtension;

public record SpawnPos(double x, double y, double z, int dim) {
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getDim() {
        return dim;
    }

    public boolean isValidPos() {
        return dim != -1;
    }
}
