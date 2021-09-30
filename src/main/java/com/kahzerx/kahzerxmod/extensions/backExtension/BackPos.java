package com.kahzerx.kahzerxmod.extensions.backExtension;

public record BackPos(double x, double y, double z, String dim) {
    public boolean isValid() {
        return dim != null && !dim.equals("");
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getDim() {
        return dim;
    }
}
